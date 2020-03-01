package myapp.view.headerParts;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
 * View with the Title of the education.
 */
public class TitleView extends BorderPane implements ViewMixin, PmMixin {
    /**
     * textField with the title of the education.
     */
    private TextField educationTextField;

    /**
     * the single entry point to the PresentationModel-Layer.
     */
    private final ClientDolphin clientDolphin;
    /**
     * get the selected education which will edited.
     */
    private final ClientPresentationModel educationProxy;

    /**
     * Constructor of this view.
     * @param clientDolphin user which connect to the serverModel
     *                      (with one accesPoints).
     */
    public TitleView(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        educationProxy = getProxyPM(PMDescription.EDUCATION);
        init();
    }

    @Override
    public void initializeParts() {
        educationTextField       = GuiHelper.getTextField("", "employeeField", "title");
        educationTextField.setPromptText("Ausbildung");
        educationTextField.setAlignment(Pos.CENTER);
    }

    @Override
    public void layoutParts() {
        setCenter(educationTextField);
    }

    @Override
    public void setupBindings() {
        educationTextField.prefWidthProperty().bind(this.widthProperty());

        //bind the value from the Model to the server, so that
        // the user is informed about all changes from the server
        JFXBinder.bind(EducationAtt.EDUCATIONNAME.name())
                .of(educationProxy)
                .to(TEXT)
                .of(educationTextField);
    }

    @Override
    public void setupValueChangedListeners() {
        //listen to the name of the education: if it
        // change in the view, it will also changed in the model.
        educationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            educationProxy.getAt(EducationAtt.EDUCATIONNAME.toString()).setValue(educationTextField.getText());
        });
    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }
}