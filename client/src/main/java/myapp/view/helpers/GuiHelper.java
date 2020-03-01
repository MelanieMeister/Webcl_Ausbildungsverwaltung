package myapp.view.helpers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class GuiHelper {
    public static TextField getTextField(String placeholder, String... cssClasses) {
        TextField textField = new TextField(placeholder);
        for (int i = 0; i != cssClasses.length; i++) {
            textField.getStyleClass().add(cssClasses[i]);
        }
        GridPane.setHgrow(textField, Priority.ALWAYS);
        textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return textField;
    }

    public static ComboBox<String> getStatusComboBox(){
        ObservableList<String> statusOptions =
                FXCollections.observableArrayList("Revers beim Mitarbeitenden", "Weiterbildung läuft", "Weiterbildung beendet, Transfersicherung ausstehend", "Abgeschlossen");

        ComboBox<String> comboBox = new ComboBox<>(statusOptions);
        GridPane.setHgrow(comboBox,Priority.ALWAYS);
        return comboBox;
    }

    public static ComboBox<String> getOrganisationComboBox(){
        ObservableList<String> organisations =
                FXCollections.observableArrayList("AGG","AGI","AÖV","AUE","AWA","GS" ,"RA" ,"TBA");
        ComboBox<String> comboBox = new ComboBox<>(organisations);

        return comboBox;
    }


    public static Label getLabel(String name,String... cssClasses) {
        Label label = new Label(name);
        for (int i = 0; i != cssClasses.length; i++) {
            label.getStyleClass().add(cssClasses[i]);
        }
        GridPane.setHgrow(label,Priority.ALWAYS);
        return label;
    }


    public static TextField getValueTextField(String placeholder, String... cssClasses) {
        TextField textField = new TextField();
        textField.getStyleClass().add("valueTextField");
        textField.setPromptText(placeholder);

        textField.setPrefWidth(80);
        textField.setMinWidth(80);
        for (int i = 0; i != cssClasses.length; i++) {
            textField.getStyleClass().add(cssClasses[i]);
        }
        textField.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return textField;
    }

    public static HBox getMainTitle(Label label){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(0,0,8,5));
        ImageView imageView = new ImageView(new Image("../resources/images/applicationIcon_small.png"));
        hbox.setSpacing(10);
        label.setFont(Font.loadFont("../resources/fonts/Lato/Lato-Bol.ttf", 20));
        hbox.getChildren().addAll(imageView, label);

        return hbox;
    }

    public static Label getMainLabel(String name) {
        Label label = new Label(name);
        label.getStyleClass().add("tileHeaderPart");
        return label;
    }

    /**
     * create a default datePicker.
     * @return
     */
    public static DatePicker getDatePicker(){
        DatePicker datePicker = new DatePicker();
        datePicker.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        GridPane.setHgrow(datePicker, Priority.ALWAYS);

        return datePicker;
    }


    /**
     * get Button for the ToolBar
     * @param cssId
     * @return
     */
    public static Button getTopButton(String cssId) {
        Button button = new Button();
        button.setId(cssId);
        button.getStyleClass().add("topButtons");
        button.setPrefSize(40,40);


        button.setTextAlignment(TextAlignment.CENTER);
        button.setAlignment(Pos.CENTER);

        return button;
    }

    public static Label getTopLabel(String name){
        Label label = new Label(name);
        label.getStyleClass().add("topLabel");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        return label;
    }

    public static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static Date convertToDate(LocalDate dateInput){
        Instant instant = Instant.from(dateInput.atStartOfDay(ZoneId.of("GMT")));
        Date date = Date.from(instant);

        SimpleDateFormat  formatter = new SimpleDateFormat("d/M/y");
        formatter.format(date);


        return date;
    }

}
