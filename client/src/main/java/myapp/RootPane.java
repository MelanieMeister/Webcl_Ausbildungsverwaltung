package myapp;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.education.EducationCommands;
import myapp.util.ViewMixin;
import myapp.view.Toolbar;
import myapp.view.headerParts.*;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

/**
 * Implementation of the view details, event handling, and binding.
 *
 */
class RootPane extends BorderPane implements ViewMixin, PmMixin {

    // clientDolphin is the single entry point to the PresentationModel-Layer
    private final ClientDolphin clientDolphin;
    private final ClientPresentationModel personProxy;
    private Toolbar toolbar;
    private GridPane headerGridPane;
    private ChartView chartView;
    /**
     * View with the Title of the education.
     */
    private TitleView titleView;
    /**
     * View where the data about the employee
     * are to set.
     */
    private PersonView personView;
    private ParticipationView participationView;

    RootPane(ClientDolphin clientDolphin, ClientPresentationModel personProxy) {
        this.clientDolphin = clientDolphin;
        this.personProxy = personProxy;
        init();
    }

    @Override
    public void initializeSelf() {
        addStylesheetFiles("/fonts/fonts.css", "/myapp/myApp.css");
        getStyleClass().add("root-pane");
    }

    @Override
    public void initializeParts() {
        toolbar = new Toolbar(getSelectedIDAttribute(PMDescription.EDUCATION),
                clientDolphin, PMDescription.EDUCATION_MASTER);

        headerGridPane = new GridPane();
        titleView = new TitleView(clientDolphin);
        chartView = new ChartView(clientDolphin);
        personView = new PersonView(clientDolphin);
        participationView = new ParticipationView(clientDolphin);

    }

    @Override
    public void layoutParts() {
        BorderPane detail = new BorderPane();
        detail.getStyleClass().add("detail");

        headerGridPane.add(titleView, 0, 0, 3, 1);
        headerGridPane.add(personView, 0, 1);
        headerGridPane.add(participationView, 1, 1);
        headerGridPane.add(chartView, 2, 1);


        participationView.prefWidthProperty().bind(headerGridPane.widthProperty().multiply(0.3));

        chartView.prefWidthProperty().bind(headerGridPane.widthProperty().multiply(0.4));

        personView.prefWidthProperty().bind(headerGridPane.widthProperty().multiply(0.3));

        headerGridPane.setVgap(15);
        headerGridPane.setId("topGrid");

        setMargin(titleView, new Insets(30, 0, 30, 0));

        headerGridPane.setHgrow(chartView, Priority.ALWAYS);
        headerGridPane.setHgrow(titleView, Priority.ALWAYS);
        headerGridPane.setHgrow(participationView, Priority.ALWAYS);
        headerGridPane.setHgrow(chartView, Priority.ALWAYS);

        setTop(toolbar);
        setCenter(headerGridPane);
    }


    @Override
    public void setupBindings() {

    }


    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }
}