package myapp.presentationmodel.education;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.Definition;

/**
 * specific PresentationModel-Attributes which are
 * show in the TableView of the Client.
 */
public enum EducationMasterAtt implements AttributeDescription {
    ENTITY_ID(),
    ENTITY_DIRTY(Definition.DIRTY_STATUS()),
    FIRSTNAME(),
    NAME(),
    ORGANISATION(),
    EDUCATIONNAME(),

    STARTEDUCATION(),
    FINISTHEDUCATION(),

    COSTPARTICIPATION();



    private Definition definition;

    EducationMasterAtt(){
        this(null);
        definition = PMDescription.EDUCATION.getAttributeDescription(name()).getDefinition();
    }

    EducationMasterAtt(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.EDUCATION_MASTER;
    }

}
