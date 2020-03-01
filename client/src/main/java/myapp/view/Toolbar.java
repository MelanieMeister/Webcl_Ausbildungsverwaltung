package myapp.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.presentationmodel.education.EducationCommands;
import myapp.presentationmodel.undoredo.UndoRedoCommands;
import myapp.util.BasicCommands;
import myapp.util.Language;
import myapp.util.ViewMixin;
import myapp.view.helpers.GuiHelper;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import static com.sun.org.apache.xml.internal.serialize.Method.TEXT;


public class Toolbar extends BorderPane implements ViewMixin, PmMixin {
    private final ClientAttribute selectedIdAttribute;
    private final ClientDolphin clientDolphin;
    private final ClientPresentationModel applicationStatePM;
    private final PMDescription pmDescription;

    private Button saveButton,
            deleteButton,  addButton,
    nextButton,previousButton;

    private ComboBox languageBox;
    private Label saveLabel;
    private Label addLabel;
    private Label deleteLabel, nextLabel, previousLabel;
    private GridPane hBoxLeft;

    public Toolbar(ClientAttribute selectedIdAttribute,
                   ClientDolphin clientDolphin, PMDescription pmDescription) {
        this.clientDolphin = clientDolphin;
        this.selectedIdAttribute = selectedIdAttribute;
        this.applicationStatePM = getProxyPM(PMDescription.APPLICATION_STATE);


        this.pmDescription = pmDescription;



        setId("topHeader");
        init();
    }


    @Override
    public void initializeSelf() {
        getStyleClass().add("standard-toolbar");
        setMargin(this, new Insets(30, 0, 30, 0));
    }

    @Override
    public void initializeParts() {
        hBoxLeft = new GridPane();

        saveLabel = GuiHelper.getTopLabel("Speichern");

        nextLabel = GuiHelper.getTopLabel("Next");
        previousLabel = GuiHelper.getTopLabel("Previous");
        deleteLabel = GuiHelper.getTopLabel("Löschen");
        addLabel = GuiHelper.getTopLabel("Neu");
        saveButton = GuiHelper.getTopButton("saveButton");
        addButton = GuiHelper.getTopButton("addEducationButton");
        previousButton = GuiHelper.getTopButton("previousButton");
        nextButton = GuiHelper.getTopButton("nextButton");
        deleteButton = GuiHelper.getTopButton("deleteEducationButton");


        ObservableList<String> languageOptions =
                FXCollections.observableArrayList("Deutsch", "Franz");

        languageBox = new ComboBox(languageOptions);
        GridPane.setValignment(languageBox, VPos.BASELINE);

    }

    @Override
    public void layoutParts() {
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(80);
        hBoxLeft.getColumnConstraints().addAll(col1, col1, col1, col1, col1, col1, col1);

        hBoxLeft.add(getVBox(addButton, addLabel, "Neue Weiterbildung erfassen"), 0, 0, 1, 2);
        hBoxLeft.add(getVBox(saveButton, saveLabel, "Änderungen speichern"), 1, 0, 1, 2);
        hBoxLeft.add(getVBox(deleteButton, deleteLabel, "Weiterbildung löschen"), 2, 0, 1, 2);
        hBoxLeft.add(getVBox(previousButton, previousLabel, "Zur nächsten Weiterbildug"), 3, 0, 1, 2);
        hBoxLeft.add(getVBox(nextButton, nextLabel, "Zur vorherigen Weiterbildug"), 4, 0, 1, 2);

        setLeft(hBoxLeft);
    }

    @Override
    public void setupEventHandlers() {
        //send a ping from the view to the Controller
        saveButton.setOnAction($ -> clientDolphin.send(EducationCommands.SAVE));
        addButton.setOnAction($ -> clientDolphin.send(EducationCommands.CREATE));

        previousButton.setOnAction($ -> clientDolphin.send(EducationCommands.SELECT_PREVIOUS));
        nextButton.setOnAction($ -> clientDolphin.send(EducationCommands.SELECT_NEXT));
    }


    @Override
    public void setupValueChangedListeners() {
        DolphinAttributeListener<Long> selectedIdListener = (attribute, oldValue, newValue)
                -> deleteButton.setDisable(PMDescription.EMPTY_SELECTION_ID == newValue);

        addListener(selectedIdAttribute, selectedIdListener);
    }



    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    private VBox getVBox(Button button, Label label, String tooltip) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(button, label);
        vBox.setAlignment(Pos.CENTER);

        final Tooltip exportTooltip = new Tooltip();
        exportTooltip.setText(tooltip);
        button.setTooltip(exportTooltip);
        label.setTooltip(exportTooltip);

        return vBox;
    }


}
