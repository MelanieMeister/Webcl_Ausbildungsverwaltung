package myapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.BasePresentationModel;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;

import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.service.TranslationService;

/**
 * Base class for all controllers.
 *
 * Defines several template-methods to initialize a controller properly.
 *
 * Provides some convenient helper-methods.
 */
public abstract class Controller extends DolphinServerAction implements DTOMixin, PmMixin {
    private final TranslationService translationService;
    private ActionRegistry actionRegistry;

    protected Controller(TranslationService translationService){
        this.translationService = translationService;

    }

    @Override
    public final void registerIn(ActionRegistry registry) {
        actionRegistry = registry;
        subscribeToCommand(BasicCommands.INITIALIZE_BASE_PMS  , this::initializeBasePMs);
        subscribeToCommand(BasicCommands.INITIALIZE_CONTROLLER, this::initializeController);

        specifyAllCommandSubscriptions();
    }

    /**
     * Used for registering all the controller specific commands and the corresponding actions.
     *
     */
    protected abstract void specifyAllCommandSubscriptions();


    /**
     * Base-PMs are needed to start up the application.
     *
     * Typically the 'ApplicationState' and all 'ProxyPMs' are Base-PMs
     */
    protected abstract void initializeBasePMs();

    /**
     * Everything that needs to be done to get a controller that is ready to be used in the application.
     */
    public void initializeController() {
        initializeSelf();
        setupModelStoreListener();
        setupValueChangedListener();
        setupBinding();
        setDefaultValues();
        rebaseAll();
    }

    protected void initializeSelf() {
    }

    /**
     * Modelstore-Listeners observe the modelstore and are notified when a presentation-model of a specific pm-type
     * is added to or removed from the modelstore.
     *
     * The controller's modelStoreListeners are specified here.
     */
    protected void setupModelStoreListener() {
    }

    /**
     * ValueChangedListeners observe a single property.
     *
     * Often the 'business-logic' is triggered by a valueChange.
     *
     * The controller's valueChangeListeners are specified here.
     */
    protected void setupValueChangedListener() {
    }

    /**
     * Bindings keep two properties in sync.
     *
     * The controller's bindings are specified here.
     */
    protected void setupBinding() {
    }

    /**
     * Used to set default values of the controller's Base-PMs
     */
    protected void setDefaultValues() {
    }


    /**
     * Creates a new PresentationModel based on the description with all the attribute values set to the DTO's slots.
     *
     * @param pmDescription the description that's used to create a new PresentationModel
     * @param dto all the necessary slots and initial values
     * @return a new PresentationModel instance
     */

    protected ServerPresentationModel createPM(PMDescription pmDescription, DTO dto) {
        long       id    = entityId(dto);
        List<Slot> slots = new ArrayList<>();
        slots.addAll(dto.getSlots());
        slots.addAll(createAllSlots(pmDescription, id, false, false));

        return createServerPresentationModel(pmDescription, id, new DTO(slots));
    }

    protected ServerPresentationModel createNewPM(PMDescription pmDescription, long id) {
        DTO dto = createDTO(pmDescription, id);

        List<Slot> additonalSlots = createAllSlots(pmDescription, id, false, false);
        dto.getSlots().addAll(additonalSlots);

        return createServerPresentationModel(pmDescription, id, dto);
    }

    private ServerPresentationModel createServerPresentationModel(PMDescription pmDescription, long id, DTO dto) {
        return getServerDolphin().presentationModel(pmDescription.pmId(id),
                                                    pmDescription.pmName(),
                                                    dto);
    }

    /**
     * Returns a PresentationModel with all additional information, e.g. 'value', 'mandatory', 'valid', for every
     * AttributeDescription of the given PMDescription
     *
     * @param pmDescription the description that's used to create a new PresentationModel
     * @return a new PresentationModel instance
     */
    protected ServerPresentationModel createProxyPM(PMDescription pmDescription) {
        ServerPresentationModel proxy = getServerDolphin().getAt(pmDescription.getProxyPmId());

        if (proxy == null) {
            proxy = getServerDolphin().presentationModel(pmDescription.getProxyPmId(),
                    "Proxy:" + pmDescription.pmName(),
                    new DTO(createAllSlots(pmDescription,
                            pmDescription.getProxyId(),
                            true, true)));
            ;
        }

        return proxy;
    }

    protected ServerPresentationModel createSinglePM(PMDescription pmDescription) {

        return getServerDolphin().presentationModel(pmDescription.getProxyPmId(),
                                                    pmDescription.pmName(),
                                                    new DTO(createAllSlots(pmDescription,
                                                                           pmDescription.getProxyId(),
                                                                           true,
                                                                           true)));
    }

    protected ServerPresentationModel createNullObjectPM(PMDescription description) {
        String pmId = description.pmId(PMDescription.EMPTY_SELECTION_ID);
        List<Slot> slots = createAllSlots(description, PMDescription.EMPTY_SELECTION_ID, true, false);
        ServerPresentationModel nullObjectPM = getServerDolphin().presentationModel(pmId, "NullObject:" + description.pmName(), new DTO(slots));

        nullObjectPM.getAttributes().stream()
                    .filter(serverAttribute -> AdditionalTag.READ_ONLY.equals(serverAttribute.getTag()))
                    .forEach(serverAttribute -> {
                        serverAttribute.setValue(true);
                        serverAttribute.rebase();
                    });

        return nullObjectPM;
    }

    /**
     * All the dirty PresentationModels based on the given PMDescription are rebased.
     *
     * This method is typically called after successfully saving the data.
     *
     * @param pmDescription description for the PresentationModels to be rebased
     */
    protected void rebase(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::rebase);
    }

    /**
     * Resets all Attributes to its initial (or persisted) value.
     *
     * @param pmDescription description for the PresentationModels to be resetted
     */
    protected void revert(PMDescription pmDescription) {
        dirtyModels(pmDescription).forEach(ServerPresentationModel::reset);
    }

    /**
     * For all dirty PresentationModels of the given PMDescription a DTO is returned.
     *
     * The DTO contains Slots for the dirty Attributes only.
     *
     * @param pmDescription description for the PresentationModels to be checked
     * @return a List of DTOs for all dirty PresentationModels
     */
    protected List<DTO> dirtyDTOs(PMDescription pmDescription) {
        List<ServerPresentationModel> dirtyPMs = dirtyModels(pmDescription);

        return dirtyPMs.stream()
                       .map(pm -> pm.getAttributes().stream()
                                    .filter(BaseAttribute::isDirty)
                                    .map(att -> new Slot(att.getPropertyName(),
                                                         att.getValue(),
                                                         att.getQualifier()))
                                    .collect(Collectors.toList()))
                       .map(DTO::new)
                       .collect(Collectors.toList());
    }

    protected List<DTO> allDTOs(PMDescription pmDescription){
        List<ServerPresentationModel> pms = getServerDolphin().findAllPresentationModelsByType(pmDescription.pmName());

        return pms.stream()
                       .map(pm -> pm.getAttributes().stream()
                                    .filter(attr -> Tag.VALUE.equals(attr.getTag()))
                                    .map(att -> new Slot(att.getPropertyName(),
                                                         att.getValue(),
                                                         att.getQualifier()))
                                    .collect(Collectors.toList()))
                       .map(DTO::new)
                       .collect(Collectors.toList());
    }

    protected void translate(ServerPresentationModel proxyPM, Language language) {
        String type = proxyPM.getPresentationModelType();
        String pmType = type.contains(":") ? type.split(":")[1] : type;
        proxyPM.getAttributes().stream()
               .filter(att -> att.getTag().equals(Tag.LABEL))
               .forEach(att -> {
                   String propertyName = att.getPropertyName();
                   att.setValue(translationService.translate(pmType, propertyName, language));
               });
    }

    protected final void subscribeToCommand(String command, Runnable performer){
        actionRegistry.register(command, ($, $$) -> performer.run());
    }

    protected final void subscribeToCommand(Class command, Consumer<Command> performer){
        actionRegistry.register(command, (cmd, $$) -> performer.accept(cmd));
    }

    /**
     * All existing PresentationModels are rebased.
     */
    private void rebaseAll() {
        Collection<ServerPresentationModel> allPMs = getServerDolphin().getServerModelStore().listPresentationModels();
        allPMs.forEach(ServerPresentationModel::rebase);
    }

    private List<ServerPresentationModel> dirtyModels(PMDescription pmDescription) {
        return getServerDolphin().findAllPresentationModelsByType(pmDescription.pmName())
                                 .stream()
                                 .filter(BasePresentationModel::isDirty)
                                 .collect(Collectors.toList());
    }

    private List<Slot> createAllSlots(PMDescription pmDescription, long entityId, boolean createValueSlot, boolean createLabelSlot) {
        List<Slot> slots = new ArrayList<>();

        Arrays.stream(pmDescription.getAttributeDescriptions()).forEach(att -> {
            String qualifier = att.qualifier(entityId);
            Object initialValue = att.getInitialValue();
            if(createValueSlot){
                slots.add(new Slot(att.name(), initialValue, qualifier, Tag.VALUE));
            }

            if(createLabelSlot){
                slots.add(new Slot(att.name(), att.name().toLowerCase(), att.labelQualifier(), Tag.LABEL));
            }
            slots.add(new Slot(att.name(), att.isInitiallyMandatory(), qualifier + ":Mandatory"        , Tag.MANDATORY));
            slots.add(new Slot(att.name(), att.isInitiallyReadOnly() , qualifier + ":ReadOnly"         , AdditionalTag.READ_ONLY));
            slots.add(new Slot(att.name(), true                      , qualifier + ":Valid"            , AdditionalTag.VALID));
            slots.add(new Slot(att.name(), "OK!"                     , qualifier + ":ValidationMessage", AdditionalTag.VALIDATION_MESSAGE));
        });

        return slots;
    }


    @Override
    public Dolphin getDolphin() {
        return getServerDolphin();
    }


}
