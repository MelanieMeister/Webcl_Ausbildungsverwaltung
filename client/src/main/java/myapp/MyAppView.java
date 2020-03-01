package myapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.applicationstate.ApplicationStateAtt;
import myapp.presentationmodel.education.EducationCommands;
import myapp.util.BasicCommands;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

/**
 * The main view of MyApp.
 */

public class MyAppView extends Application implements PmMixin {
    static ClientDolphin clientDolphin;

    @Override
    public void start(Stage stage) {
        clientDolphin.send(BasicCommands.INITIALIZE_BASE_PMS);
        clientDolphin.send(BasicCommands.INITIALIZE_CONTROLLER,
                $ -> {
                    buildUI(stage);
                    clientDolphin.send(EducationCommands.LOAD_ALL);
                    clientDolphin.startPushListening(EducationCommands.ON_PUSH, EducationCommands.ON_RELEASE);
                });
    }

    private void buildUI(Stage stage) {
        ClientPresentationModel personProxy = getProxyPM(PMDescription.EDUCATION);

        Pane root = new RootPane(clientDolphin, personProxy);
        Scene scene = new Scene(root, 1800, 600);

        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("applicationIcon.png")));
        JFXBinder.bind(ApplicationStateAtt.APPLICATION_TITLE.name(), Tag.LABEL).of(getProxyPM(PMDescription.APPLICATION_STATE))
                .to("title").of(stage);

        stage.show();
    }


    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }
}