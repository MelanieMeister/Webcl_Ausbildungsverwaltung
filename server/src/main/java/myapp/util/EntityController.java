package myapp.util;

import groovyx.gpars.dataflow.DataflowQueue;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.service.EntityService;
import myapp.service.TranslationService;
import org.opendolphin.core.*;
import org.opendolphin.core.server.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static myapp.presentationmodel.PMDescription.EMPTY_SELECTION_ID;


public abstract class EntityController extends Controller {
    public static final String TRANSFERRED_VALUE = "transferredValue";
    public static final String TYPE = "Type";
    public static final String CREATE = "create";
    public static final String DELETE = "delete";
    private static final Map<String, DTO> ATTRIBUTES_IN_WORK = new ConcurrentHashMap<>();
    private static final List<Long> CREATED_PMS = Collections.synchronizedList(new ArrayList<>());
    private static final List<Long> DELETED_PMS = Collections.synchronizedList(new ArrayList<>());
    private static final String ENTITY_ID = "entityId";
    private static final String QUALIFIER = "qualifier";
    private static final String VALUE = "value";
    private static final String RELEASE = "release";
    private static final String UPDATE = "update";
    private static final String SAVED = "saved";
    private static final String REVERTED = "reverted";
    private final DataflowQueue<DTO> dataFlowQueue;
    private final PMDescription pmMasterDescription;
    private final PMDescription pmDetailDescription;
    private final EntityService service;
    private final EventBus eventBus;
    private final Map<ServerAttribute, PropertyChangeListener> proxyListeners;
    private final PropertyChangeListener dirtyStateListener = $ -> updateCleanDataStatus();
    private ServerPresentationModel applicationStatePM;
    private boolean silent = false;
    private ServerPresentationModel masterProxyPM;
    private ServerPresentationModel detailProxyPM;

    protected EntityController(PMDescription pmMasterDescription, PMDescription pmDetailDescription,
                               EntityService service, TranslationService translationService,
                               EventBus eventBus) {
        super(translationService);

        this.pmMasterDescription = pmMasterDescription;
        this.pmDetailDescription = pmDetailDescription;


        this.service = service;
        this.eventBus = eventBus;

        this.proxyListeners = new HashMap<>();

        dataFlowQueue = new DataflowQueue<>();
        eventBus.subscribe(dataFlowQueue);
    }

    @Override
    public void specifyAllCommandSubscriptions() {
        subscribeToCommand(BasicCommands.SAVE, this::saveOnUserRequest);
        subscribeToCommand(BasicCommands.RESET, this::revertOnUserRequest);

    }

    @Override
    public void initializeBasePMs() {
        createNullObjectPM(pmDetailDescription);
        masterProxyPM = createProxyPM(pmMasterDescription);
        detailProxyPM = createProxyPM(pmDetailDescription);

    }

    @Override
    protected void initializeSelf() {
        applicationStatePM = getServerDolphin().getAt(PMDescription.APPLICATION_STATE.pmId(PMDescription.APPLICATION_STATE.getProxyId()));

        registerAllProxyListeners(getDetailProxyPM());

        PropertyChangeListener broadcaster = evt -> {
            synchronized (ATTRIBUTES_IN_WORK) {
                if (silent) {
                    return;
                }
                ServerAttribute attribute = (ServerAttribute) evt.getSource();
                if (attribute.getQualifier() != null) {

                    DTO dto = new DTO(new Slot(ENTITY_ID, pmDetailDescription.getEntityId(attribute.getPresentationModel())),
                            new Slot(QUALIFIER, attribute.getQualifier()),
                            new Slot(VALUE, attribute.getValue()));

                    if (attribute.isDirty()) {
                        ATTRIBUTES_IN_WORK.put(attribute.getQualifier(), dto);
                    } else {
                        ATTRIBUTES_IN_WORK.remove(attribute.getQualifier());
                    }

                    publish(UPDATE, dto);
                }
            }
        };

        detailProxyPM.getAttributes().stream()
                .filter(attr -> Objects.equals(attr.getTag(), Tag.VALUE))
                .forEach(attr -> attr.addPropertyChangeListener(Attribute.VALUE, broadcaster));
    }

    @Override
    protected void setupModelStoreListener() {
        getServerDolphin().addModelStoreListener(pmDetailDescription.pmName(), event -> {
            PresentationModel pm = event.getPresentationModel();
            if (event.getType() == ModelStoreEvent.Type.ADDED) {
                pm.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY, dirtyStateListener);
            } else {
                pm.removePropertyChangeListener(PresentationModel.DIRTY_PROPERTY, dirtyStateListener);
            }
        });
    }

    @Override
    protected void setupValueChangedListener() {
        super.setupValueChangedListener();

        addProxyListeners();

        selectedIDAttribute().addPropertyChangeListener(Attribute.VALUE,
                evt -> select(evt.getNewValue() != null ?
                        (long) evt.getNewValue() :
                        EMPTY_SELECTION_ID));

        Attribute languageAttribute = getProxyPM(PMDescription.APPLICATION_STATE).getAt(ApplicationStateAtt.LANGUAGE.name());
        languageAttribute.addPropertyChangeListener(Attribute.VALUE,
                evt -> translate(detailProxyPM, Language.valueOf((String) evt.getNewValue())));

    }


    protected void registerAllProxyListeners(ServerPresentationModel proxy) {
    }

    protected ServerPresentationModel getDetailPM(long entityId) {
        ServerPresentationModel detailPM = getDetailPMFromModelStore(entityId);

        if (detailPM == null) {
            DTO dto = (CREATED_PMS.contains(entityId)) ?
                    createDTO(pmDetailDescription, entityId) :
                    service.loadDetails(entityId);


            detailPM = createPM(pmDetailDescription, dto);
            detailPM.getAttributes()
                    .forEach(this::updateIfInWork);

            ServerPresentationModel masterPM = getMasterPMFromModelStore(entityId);
            if (masterPM == null) {
                masterPM = createNewPM(pmMasterDescription, entityId);
                masterPM.syncWith(detailPM);
            }

            BaseAttribute dirtyStatusAttribute = pmMasterDescription.getDirtyStatusAttribute(masterPM);
            if (dirtyStatusAttribute != null) {
                dirtyStatusAttribute.setValue(detailPM.isDirty());
                detailPM.addPropertyChangeListener(PresentationModel.DIRTY_PROPERTY,
                        evt -> dirtyStatusAttribute.setValue(evt.getNewValue()));
            }
        }
        return detailPM;
    }




    /**
     * load all education from the database.
     */
    public synchronized void loadAll() {
        List<DTO> allDtos = service.loadAllMasters();

        allDtos.removeIf(dto -> DELETED_PMS.contains(entityId(dto)));

        allDtos.addAll(CREATED_PMS.stream()
                .map(entityId -> createDTO(pmMasterDescription, entityId))
                .collect(Collectors.toList()));

        allDtos.forEach(dto -> createPM(pmMasterDescription, dto));

        ATTRIBUTES_IN_WORK.values().stream()
                .map(this::getEntityId)
                .forEach(this::getDetailPM);


        selectedIDAttribute().setValue(allDtos.isEmpty() ? PMDescription.EMPTY_SELECTION_ID : entityId(allDtos.get(0)));

        updateCleanDataStatus();
    }

    /**
     * creade a new education and it to the list with
     * the new created educations which will be saved.
     */
    protected synchronized void createOnUserRequest() {
        long newId = createNewId();
        CREATED_PMS.add(newId);

        create(newId);

        setSelectedId(newId);

        updateCleanDataStatus();

        publish(CREATE, newId);
    }

    /**
     * create a new MasterDescription (a new education) which
     * has the input id.
     * @param newId the id of the new object
     */
    private void create(long newId) {
        createNewPM(pmMasterDescription, newId);
        getDetailPM(newId);
    }

    /**
     * delete the selected education: check at first
     * if it is an education which was created and not
     * saved. if it already saved, it will be removed from
     * the object which should be saved.
     */
    protected synchronized void deleteOnUserRequest() {
        long toBeDeleted = getSelectedID();

        if (CREATED_PMS.contains(toBeDeleted)) {
            CREATED_PMS.remove(toBeDeleted);
        } else {
            DELETED_PMS.add(toBeDeleted);
        }

        delete(toBeDeleted);

        updateCleanDataStatus();publish(DELETE, toBeDeleted);
    }


    private void delete(long entityId) {
        if (entityId == getSelectedID()) {
            selectImmediateNeighbor(entityId);
        }

        getServerDolphin().remove(getMasterPMFromModelStore(entityId));
        getServerDolphin().remove(getDetailPMFromModelStore(entityId));
    }


    public synchronized void saveOnUserRequest() {
        pauseBroadCasting();

        List<DTO> dtos = dirtyDTOs(pmDetailDescription);
        service.update(dtos, CREATED_PMS, DELETED_PMS);

        rebase();

        DELETED_PMS.clear();
        CREATED_PMS.clear();

        updateCleanDataStatus();

        resumeBroadCasting();

        publish(SAVED);
    }

    private void rebase() {
        rebase(pmDetailDescription);
        rebase(pmMasterDescription);
    }

    protected synchronized void revertOnUserRequest() {
        pauseBroadCasting();

        revert();

        resumeBroadCasting();

        publish(REVERTED);
    }

    private void revert(){
        CREATED_PMS.forEach(this::delete);
        DELETED_PMS.forEach(this::getDetailPM);

        revert(pmDetailDescription);
        revert(pmMasterDescription);
    }

    /**
     * select the next education and if there aren't
     * any next education, the previous education will be selected.
     * @param entityId show the start position
     */
    private void selectImmediateNeighbor(long entityId) {
        List<ServerPresentationModel> allMasters = allMasterPmsInModelStore();

        long toBeSelected = EMPTY_SELECTION_ID;
        if (allMasters.size() >= 2) {
            if (pmMasterDescription.getEntityId(allMasters.get(0)) == entityId) {
                toBeSelected = pmMasterDescription.getEntityId(allMasters.get(1));
            } else {
                for (int i = 0; i < allMasters.size() - 1; i++) {
                    long id = pmMasterDescription.getEntityId(allMasters.get(i + 1));
                    if (id == entityId) {
                        toBeSelected = pmMasterDescription.getEntityId(allMasters.get(i));
                        break;
                    }
                }
            }
        }

        setSelectedId(toBeSelected);
    }
    /**
     * select the next education. if it has any next
     * education, the user still remain on the same education (if
     * the current selected education is the last education).
     */
    protected synchronized void selectNext() {
        long currentId = getSelectedID();

        List<ServerPresentationModel> allPMs = allMasterPmsInModelStore();

        ServerPresentationModel next = null;
        AttributeDescription<Long> entityIDDescription = pmMasterDescription.getEntityIDDescription();

        if (currentId == PMDescription.EMPTY_SELECTION_ID ||
                entityIDDescription.getValueFrom(allPMs.get(allPMs.size() - 1)).equals(currentId)) {
            next = allPMs.get(0);
        } else {
            for (int i = 0; i < allPMs.size() - 1; i++) {
                if (entityIDDescription.getValueFrom(allPMs.get(i)).equals(currentId)) {
                    next = allPMs.get(i + 1);
                    break;
                }
            }
        }

        setSelectedId(entityIDDescription.getValueFrom(next));
    }

    /**
     * select the previous education. if it has any previous
     * education, the user still remain on the same education (if
     * the current selected education is the first education).
     */
    protected synchronized void selectPrevious(){
        long currentId = getSelectedID();

        List<ServerPresentationModel> allPMs = allMasterPmsInModelStore();

        ServerPresentationModel previous = null;
        AttributeDescription<Long> entityIDDescription = pmMasterDescription.getEntityIDDescription();

        if (currentId == PMDescription.EMPTY_SELECTION_ID) {
            previous = allPMs.get(0);
        } else if (entityIDDescription.getValueFrom(allPMs.get(0)).equals(currentId)) {
            previous = allPMs.get(allPMs.size() - 1);
        } else {
            for (int i = 1; i < allPMs.size(); i++) {
                if (entityIDDescription.getValueFrom(allPMs.get(i)).equals(currentId)) {
                    previous = allPMs.get(i - 1);
                    break;
                }
            }
        }

        setSelectedId(entityIDDescription.getValueFrom(previous));
    }


    protected ServerPresentationModel getDetailPMFromModelStore(long entityId) {
        return getServerDolphin().getAt(pmDetailDescription.pmId(entityId));
    }

    protected ServerPresentationModel getMasterPMFromModelStore(long entityId) {
        return getServerDolphin().getAt(pmMasterDescription.pmId(entityId));
    }


    public ServerPresentationModel getDetailProxyPM() {
        return detailProxyPM;
    }

    protected void registerProxyListener(AttributeDescription attributeDescription, PropertyChangeListener listener) {
        proxyListeners.put(detailProxyPM.getAt(attributeDescription.name()), listener);
    }


    private synchronized void select(long domainId) {
        pauseBroadCasting();
        removeProxyListeners();
        ServerPresentationModel pm = getDetailPM(domainId);
        detailProxyPM.syncWith(pm);
        addProxyListeners();
        resumeBroadCasting();
    }

    private void addProxyListeners() {
        proxyListeners.forEach(this::addListener);
    }

    private void removeProxyListeners(){
        proxyListeners.forEach((serverAttribute, listener) -> serverAttribute.removePropertyChangeListener(Attribute.VALUE, listener));
    }

    private void addListener(ServerAttribute attribute, PropertyChangeListener listener) {
        attribute.addPropertyChangeListener(Attribute.VALUE, listener);
        listener.propertyChange(new PropertyChangeEvent(attribute, attribute.getPropertyName(), attribute.getValue(), attribute.getValue()));
    }

    private void updateCleanDataStatus() {
        long dirtyPMs = getServerDolphin().findAllPresentationModelsByType(pmDetailDescription.pmName()).stream()
                .filter(BasePresentationModel::isDirty)
                .count();

        applicationStatePM.getAt(ApplicationStateAtt.CLEAN_DATA.name()).setValue(dirtyPMs == 0 &&
                DELETED_PMS.isEmpty() &&
                CREATED_PMS.isEmpty());
    }

    protected void onRelease() {
        eventBus.publish(null, new DTO(new Slot(TYPE, RELEASE)));
    }

    protected void processEventsFromQueue() {
        try {
            DTO dto = dataFlowQueue.getVal(60, TimeUnit.SECONDS);
            while (null != dto) {
                pauseBroadCasting();

                switch(getType(dto)){
                    case RELEASE:
                        break;
                    case UPDATE:
                        DTO updateInfo = getTransferredValue(dto);

                        ServerPresentationModel detailPM = getDetailPM(getEntityId(updateInfo));
                        detailPM.findAttributeByQualifier(getQualifier(updateInfo))
                                .setValue(getValue(updateInfo));

                        break;
                    case CREATE:
                        create(getTransferredValue(dto));
                        break;
                    case DELETE:
                        delete(getTransferredValue(dto));
                        break;
                    case SAVED:
                        rebase();
                        break;
                    case REVERTED:
                        revert();
                        DELETED_PMS.clear();
                        CREATED_PMS.clear();
                        break;
                }

                updateCleanDataStatus();

                resumeBroadCasting();

                dto = dataFlowQueue.getVal(20, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            // do nothing
        }
    }

    private String getType(DTO dto) {
        return getPropertyValue(dto, TYPE);
    }

    private long getEntityId(DTO dto){
        return getPropertyValue(dto, ENTITY_ID);
    }

    private String getQualifier(DTO dto){
        return getPropertyValue(dto, QUALIFIER);
    }

    private Object getValue(DTO dto){
        return getPropertyValue(dto, VALUE);
    }

    private <V> V getTransferredValue(DTO dto) {
        return getPropertyValue(dto, TRANSFERRED_VALUE);
    }

    private <V> V getPropertyValue(DTO dto, String propertyName){
        return (V) dto.getSlots().stream()
                .filter(slot -> propertyName.equals(slot.getPropertyName()))
                .map(Slot::getValue)
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }


    public void pauseBroadCasting() {
        silent = true;
    }

    public void resumeBroadCasting() {
        silent = false;
    }

    public synchronized void updateIfInWork(Attribute attribute) {
        DTO dto = ATTRIBUTES_IN_WORK.get(attribute.getQualifier());
        if(dto != null){
            attribute.setValue(getValue(dto));
        }
    }
    protected List<ServerPresentationModel> allMasterPmsInModelStore() {
        return getServerDolphin().findAllPresentationModelsByType(pmMasterDescription.pmName());
    }


    protected ServerAttribute selectedIDAttribute() {
        return applicationStatePM.getAt(pmMasterDescription.selectedIdPropertyName());
    }

    protected long getSelectedID() {
        return (long) selectedIDAttribute().getValue();
    }

    protected void setSelectedId(long id) {
        selectedIDAttribute().setValue(id);
    }
    private void publish(String type){
        publish(type, null);
    }

    private void publish(String type, Object transferredValue) {
        eventBus.publish(dataFlowQueue, new DTO(new Slot(TYPE, type),
                new Slot(TRANSFERRED_VALUE, transferredValue)));
    }
}
