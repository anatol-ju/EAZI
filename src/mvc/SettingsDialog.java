package mvc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsDialog extends Dialog<Boolean> {

    @FXML
    private Label savePath;
    @FXML
    private ChoiceBox<Integer> actionCircleFieldCount;
    @FXML
    private ToggleButton autoSaveButton;
    @FXML
    private ToggleButton hideLogButton;
    @FXML
    private TextField autoSaveIntervalField;
    @FXML
    private ColorPicker colorPickerFighter;
    @FXML
    private ColorPicker colorPickerAlly;
    @FXML
    private ColorPicker colorPickerEnemy;
    @FXML
    private HBox intervalBox;

    private ButtonType reset, buttonOk;

    public SettingsDialog() {

        reset = new ButtonType("Reset");
        buttonOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(reset);
        this.getDialogPane().getButtonTypes().add(buttonOk);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        this.setResultConverter(param -> {
            if (param.equals(buttonOk)) {
                return true;
            } else {
                return false;
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsDialog.fxml"));
        loader.setController(this);

        try {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            this.getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("Einstellungen");

        Platform.runLater(() -> {
        });
    }

    public void initialize(URL location, ResourceBundle resources) {
        // TODO replace by <settings.properties> values
        savePath.setText(System.getProperty("user.home") + "\\eazi");

        actionCircleFieldCount.getItems().addAll(12,16,20,24);
        actionCircleFieldCount.setValue(12);    // default

        autoSaveButton.setSelected(true);
        intervalBox.setDisable(false);
        autoSaveIntervalField.setText("2");

        hideLogButton.setSelected(false);

        colorPickerFighter.setValue(Color.web("#32CD32")); //LIMEGREEN
        colorPickerAlly.setValue(Color.web("#0000CD")); //MEDIUMBLUE
        colorPickerEnemy.setValue(Color.web("#FF0000")); //RED
    }
}
