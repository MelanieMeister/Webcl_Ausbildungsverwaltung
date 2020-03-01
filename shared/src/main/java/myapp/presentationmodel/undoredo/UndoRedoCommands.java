package myapp.presentationmodel.undoredo;


public interface UndoRedoCommands {
    String UNDO  = unique("undo");
    String REDO  = unique("redo");

    static String unique(String key) {
        return UndoRedoCommands.class.getName() + "." + key;
    }

}
