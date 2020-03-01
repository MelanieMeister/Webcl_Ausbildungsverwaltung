package myapp.view.headerParts;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.education.EducationAtt;
import myapp.util.ViewMixin;
import myapp.view.helpers.GuiHelper;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import static com.sun.org.apache.xml.internal.serialize.Method.TEXT;

/**
 * View where the data about the employee
 * are to set.
 */
public class PersonView extends GridPane implements ViewMixin, PmMixin {
    // clientDolphin is the single entry point to the PresentationModel-Layer
    private final ClientDolphin clientDolphin;
    private final ClientPresentationModel personProxy;
    private final ClientPresentationModel applicationState;
    /**
     * label with the title of this part of the view.
     */
    private Label titleLabel;
    /**
     * labels which inform the user which information
     * is need.
     */
    private Label firstNameLabel, lastNameLabel, numberLabel, organisationLabel;
    /**
     * textFields where the user define the data
     * of the employee.
     */
    private TextField firstNameTextField, lastNameTextField, numberTextField;

    public PersonView(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        applicationState = getProxyPM(PMDescription.APPLICATION_STATE);
        personProxy = getProxyPM(PMDescription.EDUCATION);
        init();
    }

    @Override
    public void initializeParts() {
        //title
        titleLabel = GuiHelper.getMainLabel("Person");

        //infoLabels
        firstNameLabel = getLabel("Vorname");
        lastNameLabel = getLabel("Nachname");
        numberLabel = getLabel("Pers.Nr.");
        organisationLabel = getLabel("Amt");

        //controls
        firstNameTextField = getValueTextField("Vorname");
        lastNameTextField = getValueTextField("Nachname");
        numberTextField = getValueTextField("Pers.Nr.");
    }


    @Override
    public void layoutParts() {
        add(GuiHelper.getMainTitle(titleLabel), 0, 0, 2, 1); //Termine

        //info labels
        add(firstNameLabel, 0, 1);
        add(lastNameLabel, 0, 2);
        add(numberLabel, 0, 3);

        //controls
        add(firstNameTextField, 1, 1);
        add(lastNameTextField, 1, 2);
        add(numberTextField, 1, 3);
    }

    @Override
    public void setupBindings() {
        //bind the value from the Model to the server, so that
        //the user is informed about all changes from the server
        JFXBinder.bind(EducationAtt.NAME.name())
                .of(personProxy)
                .to(TEXT)
                .of(lastNameTextField);

        JFXBinder.bind(EducationAtt.FIRSTNAME.name())
                .of(personProxy)
                .to(TEXT)
                .of(firstNameTextField);
        JFXBinder.bind(EducationAtt.PERSNR.name())
                .of(personProxy)
                .to(TEXT)
                .of(numberTextField);
    }

    @Override
    public void setupValueChangedListeners() {
        //listen to the name of the education: if it
        // change in the view, it will also changed in the model.
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.NAME.toString()).setValue(lastNameTextField.getText());
        });

        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.FIRSTNAME.toString()).setValue(firstNameTextField.getText());
        });

        numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.PERSNR.toString()).setValue(numberTextField.getText());
        });
    }


    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }


    private Label getLabel(String name) {
        Label label = GuiHelper.getLabel(name);

        label.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
        return label;
    }

    public TextField getValueTextField(String placeholder, String... cssClasses) {
        TextField textField = GuiHelper.getValueTextField(placeholder, cssClasses);
        textField.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
        return textField;
    }

}