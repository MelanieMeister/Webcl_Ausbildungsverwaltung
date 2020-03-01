package myapp.presentationmodel;

import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.presentationmodel.education.EducationAtt;
import myapp.presentationmodel.education.EducationMasterAtt;
import myapp.util.AttributeDescription;
import org.opendolphin.core.BaseAttribute;
import org.opendolphin.core.PresentationModel;

import java.util.Arrays;
import java.util.Objects;

/**
 * Specifies/describes all the PresentationModels of this application.
 * <p>
 * Although technically feasible, there shouldn't be any PresentationModel that's not described here.
 */
public enum PMDescription {
    EDUCATION("EducationPM", "small", EducationAtt.values(), null, true),
    EDUCATION_MASTER("EducationMasterPM", "small", EducationMasterAtt.values(), EDUCATION, true),


    // ApplicationState is always needed
    APPLICATION_STATE("ApplicationStatePM", null, ApplicationStateAtt.values(), null, false);

    public static long EMPTY_SELECTION_ID = -1L;
    public static long PROXY_ID = -777L;

    private final String pmName;
    private final String entityName;
    private final AttributeDescription[] attributeDescriptions;
    private final PMDescription baseDescription;
    private final boolean isBroadCasting;

    PMDescription(String pmName, String entityName, AttributeDescription[] attributeDescriptions,
                  PMDescription baseDescription, boolean isBroadCasting) {
        this.pmName = pmName;
        this.entityName = entityName;
        this.attributeDescriptions = attributeDescriptions;
        this.baseDescription = baseDescription;
        this.isBroadCasting = isBroadCasting;
    }

    public String pmName() {
        return pmName;
    }

    public String entityName() {
        return entityName;
    }

    public String selectedIdPropertyName() {
        return entityName() + "_selectedID";
    }

    public AttributeDescription[] getAttributeDescriptions() {
        return attributeDescriptions;
    }

    public PMDescription getBaseDescription() {
        return baseDescription;
    }

    public boolean isBroadcasting() {
        return isBroadCasting;
    }

    public AttributeDescription<Long> getEntityIDDescription() {
        return (AttributeDescription<Long>) Arrays.stream(attributeDescriptions)
                .filter(attributeDescription -> attributeDescription.isEntityId())
                .findAny()
                .orElse(null);
    }

    public AttributeDescription<Boolean> getDirtyStatusDescription() {
        return (AttributeDescription<Boolean>) Arrays.stream(attributeDescriptions)
                .filter(attributeDescription -> attributeDescription.isDirtyStatus())
                .findAny()
                .orElse(null);
    }

    public AttributeDescription getAttributeDescription(String name) {
        return Arrays.stream(attributeDescriptions)
                .filter(attributeDescription -> Objects.equals(attributeDescription.name(), name))
                .findAny()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String pmId(long id) {
        return pmName() + ":" + id;
    }

    public long getProxyId() {
        return PROXY_ID;
    }

    public String getProxyPmId() {
        return pmId(PROXY_ID);
    }

    public long getEntityId(PresentationModel presentationModel) {
        try{
            return (long) presentationModel.getAt(getEntityIDDescription().name()).getValue();
        }catch (ClassCastException e){
            Long l;
            Integer i;
            if(presentationModel.getAt(getEntityIDDescription().name()).getValue().toString().equals("")){
                i=0;
            }else{
                i =  Integer.parseInt((presentationModel.getAt(getEntityIDDescription().name()).getValue().toString())); //example

            }
            l = Long.valueOf(i.longValue());

            return l;
        }

    }

    public <T extends BaseAttribute> T getDirtyStatusAttribute(PresentationModel presentationModel) {
        AttributeDescription<Boolean> dirtyStatusDescription = getDirtyStatusDescription();
        return dirtyStatusDescription != null ? (T) presentationModel.getAt(dirtyStatusDescription.name()) : null;
    }
}
