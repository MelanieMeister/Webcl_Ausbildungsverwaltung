package myapp.util;

public interface BasicCommands {

    String INITIALIZE_BASE_PMS   = unique("InitializeBasePMs");
    String INITIALIZE_CONTROLLER = unique("InitializeController");
    String SAVE                  = unique("Save");
    String RESET                 = unique("Revert");

    static String unique(String key) {
        return BasicCommands.class.getName() + "." + key;
    }
}
