package myapp.presentationmodel.education;

/**
 * specify all commands you need in the corresponding controller.
 */
public interface EducationCommands {
    String LOAD_ALL = unique("loadAllPersons");
    String SELECT_NEXT = unique("selectNextPerson");
    String SELECT_PREVIOUS = unique("selectPreviousPerson");
    String CREATE = unique("create");
    String DELETE = unique("delete");
    String SAVE = unique("save");
    String ON_PUSH = unique("onPush");
    String ON_RELEASE = unique("onRelease");


    static String unique(String key) {
        return EducationCommands.class.getName() + "." + key;
    }

}
