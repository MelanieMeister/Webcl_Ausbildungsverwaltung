package myapp.presentationmodel.applicationstate;

import myapp.presentationmodel.PMDescription;
import myapp.util.AttributeDescription;
import myapp.util.Definition;
import myapp.util.validators.RegexValidator;


public enum ApplicationStateAtt implements AttributeDescription {
    // these are almost always needed
    APPLICATION_TITLE (Definition.STRING()),
    LANGUAGE          (Definition.STRING()
                                 .syntaxValidator(RegexValidator.forPattern("^((?i)english){1}|((?i)german){1}$", "UNKOWN_LANGUAGE"))),
    CLEAN_DATA        (Definition.BOOLEAN()
                                 .undoAble(false)),
    FILTER_STRING     (Definition.STRING()
                                 .undoAble(false)),
    UNDO_DISABLED     (Definition.BOOLEAN()
                                 .undoAble(false)),
    REDO_DISABLED     (Definition.BOOLEAN()
                                 .undoAble(false));


    private final Definition definition;

    ApplicationStateAtt(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    public PMDescription getPMDescription() {
        return PMDescription.APPLICATION_STATE;
    }


}
