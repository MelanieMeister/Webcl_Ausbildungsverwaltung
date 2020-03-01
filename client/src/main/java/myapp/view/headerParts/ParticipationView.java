package myapp.view.headerParts;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import myapp.presentationmodel.Example;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.education.EducationAtt;
import myapp.util.ViewMixin;
import myapp.view.helpers.GuiHelper;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static com.sun.org.apache.xml.internal.serialize.Method.TEXT;

public class ParticipationView extends GridPane implements ViewMixin, PmMixin {
    private Label participationLabel; //Beteiligung

    private Label costsLabel; //Kosten
    private Label chargeLabel; //Spesen
    private Label costsTotalLabel; //Kosten total

    private Label costEducationLabel; //Kosten Ausbildung
    private Label costParticipationLabel; //Beteiligung durch die
    private Label durationLabel;

    private TextField costsEducationTextField; //Betrag für Kosten der Ausbildung
    private TextField chargeEducationTextField; //Betrag für Spesen der Ausbildung

    private TextField costsParticipationTextField; //Betrag für die Beteiligung der Kosten durch die
    private TextField chargeParticipationTextField; //Betrag für die Beteiligung der Spesen durch die

    private Label costsTotalEducationLabel; //Betrag für Kosten (inkl. Spesen) total
    private Label costsTotalParticipationLabel;//Betrag für die Beteiligung Kosten (inkl. Spesen) total

    private TextField durationEducationTextField; //Dauer der Ausbildung
    private TextField durationParticipationTextField; //Dauer der Ausbildung, die durch die BVE übernommen wird


    // clientDolphin is the single entry point to the PresentationModel-Layer
    private final ClientDolphin clientDolphin;
    private final ClientPresentationModel personProxy;
    private final ClientPresentationModel applicationState;

    public ParticipationView(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        applicationState = getProxyPM(PMDescription.APPLICATION_STATE);
        personProxy = getProxyPM(PMDescription.EDUCATION);
        init();
        setId("participationView");

    }

    @Override
    public void initializeParts() {
        participationLabel= GuiHelper.getMainLabel("Beteiligung");
        costsLabel= GuiHelper.getLabel("Kosten");
        chargeLabel= GuiHelper.getLabel("Spesen");
        costsTotalLabel= GuiHelper.getLabel("Total", "total");
        costEducationLabel= GuiHelper.getLabel("Kosten \nAusbildung");
        costParticipationLabel= GuiHelper.getLabel("Beteiligung \ndurch die "+ Example.getCompany());
        durationLabel= GuiHelper.getLabel("Dauer");

        costsEducationTextField        = GuiHelper.getValueTextField("10000");  //Betrag für Kosten der Ausbildung
        chargeEducationTextField       = GuiHelper.getValueTextField("200");  //Betrag für Spesen der Ausbildung
        costsParticipationTextField    = GuiHelper.getValueTextField("5000");  //Betrag für die Beteiligung der Kosten durch die Firma
        chargeParticipationTextField   = GuiHelper.getValueTextField("180");  //Betrag für die Beteiligung der Spesen durch die Firma
        costsTotalEducationLabel       = GuiHelper.getLabel("10200", "total");  //Betrag für Kosten (inkl. Spesen) total
        costsTotalParticipationLabel   = GuiHelper.getLabel("5180","total");  //Betrag für die Beteiligung Kosten (inkl. Spesen) total

        durationEducationTextField= GuiHelper.getTextField("20"); //Dauer der Ausbildung
        durationParticipationTextField= GuiHelper.getTextField("10"); //Dauer der Ausbildung, die durch die Firma übernommen wird
    }

    @Override
    public void layoutParts() {
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints colSep = new ColumnConstraints();
        colSep.setMaxWidth(5);
        ColumnConstraints col2 = new ColumnConstraints();
        ColumnConstraints col3 = new ColumnConstraints();
        col1.prefWidthProperty().bind(this.widthProperty().multiply(0.14));
        col2.prefWidthProperty().bind(this.widthProperty().multiply(0.29));
        col3.prefWidthProperty().bind(this.widthProperty().multiply(0.13));
        getColumnConstraints().addAll(col1,colSep,col2,col2);

        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setId("seperator");
        separator.setStyle("-fx-padding: 5px;-fx-border-style: bold;-fx-border-width: 5px;");
        Separator separator2 = new Separator(Orientation.VERTICAL);
        separator.setId("seperator");
        separator.setStyle("-fx-padding: 5px;-fx-border-style: bold;-fx-border-width: 5px;");
        //    separator2.setOrientation(Orientation.VERTICAL);

        add(separator,0,2,5,1);
        add(separator2,1,2,1,6);
        add(GuiHelper.getMainTitle(participationLabel),0,0,4,1); //Beteiligung

        add( costsLabel,0,3); //Kosten
        add( chargeLabel,0,4); //Spesen
        add( costsTotalLabel,0,5); //Kosten total
        add( costEducationLabel,2,1); //Kosten Ausbildung  TITLE
        add( costParticipationLabel,3,1)       ; //Beteiligung durch die   TITLE
        add( durationLabel,0,7)                ;
        add(costsEducationTextField,2,3); //Betrag für Kosten der Ausbildung
        add(chargeEducationTextField,2,4); //Betrag für Spesen der Ausbildung
        add(costsParticipationTextField,3,3); //Betrag für die Beteiligung der Kosten durch die
        add(chargeParticipationTextField,3,4); //Betrag für die Beteiligung der Spesen durch die
        add(costsTotalEducationLabel,2,5); //Betrag für Kosten (inkl. Spesen) total
        add(costsTotalParticipationLabel,3,5);//Betrag für die Beteiligung Kosten (inkl. Spesen) total

        add( durationEducationTextField,2,7);
        add( durationParticipationTextField,3,7);
    }


    @Override
    public void setupValueChangedListeners() {
        JFXBinder.bind(EducationAtt.COSTEDUCATION.name())
                .of(personProxy)
                //    .using(name -> name + ", " + EducationAtt.COSTEDUCATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(costsEducationTextField);

        JFXBinder.bind(EducationAtt.COSTPARTICIPATION.name())
                .of(personProxy)
                //    .using(name -> name + ", " + EducationAtt.COSTPARTICIPATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(costsParticipationTextField);

        JFXBinder.bind(EducationAtt.CHARGEEDUCATION.name())
                .of(personProxy)
                //    .using(name -> name + ", " + EducationAtt.CHARGEEDUCATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(chargeEducationTextField);

        JFXBinder.bind(EducationAtt.CHARGEPARTICIPATION.name())
                .of(personProxy)
                //    .using(name -> name + ", " + EducationAtt.CHARGEPARTICIPATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(chargeParticipationTextField);


        JFXBinder.bind(EducationAtt.DURATIONEDUCATION.name())
                .of(personProxy)
                //  .using(name -> name + ", " + EducationAtt.DURATIONEDUCATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(durationEducationTextField);

        JFXBinder.bind(EducationAtt.DURATIONPARTICIPATION.name())
                .of(personProxy)
                //   .using(name -> name + ", " + EducationAtt.DURATIONPARTICIPATION.getValueFrom(personProxy))
                .to(TEXT)
                .of(durationParticipationTextField);


        costsParticipationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            costsTotalParticipationLabel.setText(Double.toString(Double.parseDouble(chargeParticipationTextField.getText())
                    + Double.parseDouble(costsParticipationTextField.getText())));
            personProxy.getAt(EducationAtt.COSTPARTICIPATION.toString()).setValue(costsParticipationTextField.getText());

        });

        costsEducationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
           costsTotalEducationLabel.setText(Double.toString(Double.parseDouble(chargeEducationTextField.getText()) + Double.parseDouble(costsEducationTextField.getText())));
            personProxy.getAt(EducationAtt.COSTEDUCATION.toString()).setValue(costsEducationTextField.getText());

        });

        chargeParticipationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            costsTotalParticipationLabel.setText(Double.toString(Double.parseDouble(chargeParticipationTextField.getText()) + Double.parseDouble(costsParticipationTextField.getText())));
            personProxy.getAt(EducationAtt.CHARGEPARTICIPATION.toString()).setValue(chargeParticipationTextField.getText());

        });

        chargeEducationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            costsTotalEducationLabel.setText(Double.toString(Double.parseDouble(chargeEducationTextField.getText()) + Double.parseDouble(costsEducationTextField.getText())));
            personProxy.getAt(EducationAtt.CHARGEEDUCATION.toString()).setValue(chargeEducationTextField.getText());

        });

        costsTotalEducationLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.COSTTOTALEDUCATION.toString()).setValue(costsTotalEducationLabel.getText());
        });

        costsTotalParticipationLabel.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.COSTTOTALPARTICIPATION.toString()).setValue(costsTotalParticipationLabel.getText());
        });

        durationParticipationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.DURATIONPARTICIPATION.toString()).setValue(durationParticipationTextField.getText());
        });

        durationEducationTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            personProxy.getAt(EducationAtt.DURATIONEDUCATION.toString()).setValue(durationEducationTextField.getText());
        });

    }

    public String calculateTotal(String charge, String costs){
        double chargeAmount = Double.parseDouble(charge);
        double costAmount = Double.parseDouble(costs);
        return chargeAmount + costAmount +"";
    }
    /*   organisationBox.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent actionEvent) {
                   personProxy.getAt(EducationAtt.COSTEDUCATION.toString()).setValue(organisationBox.getValue());
               }
           });*/
    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    private HBox getHBox(String name, CheckBox checkBox){
        HBox hBox = new HBox();
        Label label = new Label(name);

        hBox.getChildren().addAll(label, checkBox);
        return hBox;
    }
}
