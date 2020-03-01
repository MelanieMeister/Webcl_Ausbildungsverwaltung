package myapp.view.headerParts;


import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import myapp.presentationmodel.Example;
import myapp.presentationmodel.PMDescription;
import myapp.presentationmodel.PmMixin;
import myapp.presentationmodel.education.EducationAtt;
import myapp.util.ViewMixin;
import myapp.view.helpers.GuiHelper;
import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ChartView extends VBox implements ViewMixin, PmMixin {
    /**
     * clientDolphin is the single entry point to the PresentationModel-Layer.
     */
    private final ClientDolphin clientDolphin;
    private final ClientPresentationModel personProxy;
    private final ClientPresentationModel applicationState;
    private Label overViewTitleLabel;

    public ChartView(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
        applicationState = getProxyPM(PMDescription.APPLICATION_STATE);
        personProxy = getProxyPM(PMDescription.EDUCATION);
        init();
    }

    @Override
    public void initializeParts() {
        overViewTitleLabel = GuiHelper.getMainLabel("Übersicht");
        CategoryAxis xAxis = new CategoryAxis();

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("CHF");

        // Create a BarChart
        BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);

        // Series 1 - Data of 2014
        XYChart.Series<String, Number> dataSeries1 = new XYChart.Series<String, Number>();
        dataSeries1.setName("Anteil "+ Example.getCompany());

        dataSeries1.getData().add(new XYChart.Data<String, Number>("Kosten Ausbildung", 0)); // COSTTOTALPARTICIPATION
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Spesen Ausbildung", 0));  // CHARGEPARTICIPATION
        dataSeries1.getData().add(new XYChart.Data<String, Number>("Kosten total", 0));     // COSTPARTICIPATION


        // Series 2 - Data of 2015
        XYChart.Series<String, Number> dataSeries2 = new XYChart.Series<String, Number>();
        dataSeries2.setName("Kosten Ausbildung");

        dataSeries2.getData().add(new XYChart.Data<String, Number>("Kosten Ausbildung", 0));     //COSTTOTALEDUCATION
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Spesen Ausbildung", 0));       //CHARGEEDUCATION
        dataSeries2.getData().add(new XYChart.Data<String, Number>("Kosten total", 0));          //COSTEDUCATION

        personProxy.getAt(EducationAtt.COSTTOTALPARTICIPATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries1.getData().add(new XYChart.Data<String, Number>("Kosten Ausbildung", Double.parseDouble(evt.getNewValue().toString()))); // COSTTOTALPARTICIPATION  // COSTTOTALPARTICIPATION

            }
        });

        personProxy.getAt(EducationAtt.CHARGEPARTICIPATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries1.getData().add(new XYChart.Data<String, Number>("Spesen Ausbildung", Double.parseDouble(evt.getNewValue().toString())));  // CHARGEPARTICIPATION
            }
        });
        personProxy.getAt(EducationAtt.COSTPARTICIPATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries1.getData().add(new XYChart.Data<String, Number>("Kosten total", Double.parseDouble(evt.getNewValue().toString())));     // COSTPARTICIPATION
            }
        });


        personProxy.getAt(EducationAtt.COSTTOTALEDUCATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries2.getData().add(new XYChart.Data<String, Number>("Kosten Ausbildung", Double.parseDouble(evt.getNewValue().toString())));     //COSTTOTALEDUCATION
            }
        });
        personProxy.getAt(EducationAtt.CHARGEEDUCATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries2.getData().add(new XYChart.Data<String, Number>("Spesen Ausbildung", Double.parseDouble(evt.getNewValue().toString())));       //CHARGEEDUCATION
            }
        });
        personProxy.getAt(EducationAtt.COSTEDUCATION.toString()).addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dataSeries2.getData().add(new XYChart.Data<String, Number>("Kosten total", Double.parseDouble(evt.getNewValue().toString())));          //COSTEDUCATION
            }
        });
        // Add Series to BarChart.
        barChart.getData().add(dataSeries1);
        barChart.getData().add(dataSeries2);
        overViewTitleLabel.setMinWidth(this.getWidth());

        this.getChildren().addAll(GuiHelper.getMainTitle(overViewTitleLabel), barChart);
        //  VBox vbox = new VBox(barChart);

    }


    @Override
    public void layoutParts() {
        setPrefSize(400, 120);

    }

    @Override
    public void setupBindings() {


    }

    @Override
    public Dolphin getDolphin() {
        return clientDolphin;
    }

    @Override
    public void setupValueChangedListeners() {
    }


}
