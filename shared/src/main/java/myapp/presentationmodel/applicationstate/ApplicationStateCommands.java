package myapp.presentationmodel.applicationstate;


public interface ApplicationStateCommands {



    static String unique(String key) {
        return ApplicationStateCommands.class.getName() + "." + key;
    }
}
