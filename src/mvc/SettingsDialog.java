package mvc;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
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

    private final ResourceBundle settings = ResourceBundle.getBundle("settings.settings");

    public SettingsDialog() {

        reset = new ButtonType("Reset");
        buttonOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(reset);
        this.getDialogPane().getButtonTypes().add(buttonOk);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        // validating and saving
        final Button btOk = (Button) this.getDialogPane().lookupButton(buttonOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!validateAndStore()) {
                event.consume();
            }
        });

        // resetting to default
        final Button btRes = (Button) this.getDialogPane().lookupButton(reset);
        btRes.addEventFilter(ActionEvent.ACTION, event -> {
            resetForm();
            event.consume();
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
        setTitle(ResourceBundle.getBundle("locales.Languages").getString("settings"));

        // Platform.runLater(() -> { });
        this.showAndWait();
    }

    /**
     * Set form values to default.
     */
    private void resetForm() {
        actionCircleFieldCount.setValue(12);

        autoSaveButton.setSelected(true);
        intervalBox.setDisable(false);
        autoSaveIntervalField.setText("2");

        hideLogButton.setSelected(false);

        colorPickerFighter.setValue(Fighter.getColor()); //LIMEGREEN
        colorPickerAlly.setValue(AllyFighter.getColor()); //MEDIUMBLUE
        colorPickerEnemy.setValue(EnemyFighter.getColor()); //RED
    }

    /**
     * Used to validate form data and store them into preferences
     * before closing dialog manually.
     * @return true if properly validated.
     */
    private boolean validateAndStore() {
        // validate
        int interval = -1;
        try {
            interval = Integer.parseInt(autoSaveIntervalField.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (interval <= -1 || interval > 20) {
            return false;
        }
        // store
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("settings.settings.properties"));
            prop.setProperty("autoSaveInterval", String.valueOf(interval));
            prop.store(new FileOutputStream("settings.settings.properties"), "updated");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void initialize(URL location, ResourceBundle resources) {

        savePath.setText(System.getProperty("user.home") + settings.getString("savePath"));

        actionCircleFieldCount.getItems().addAll(12,16,20,24);
        actionCircleFieldCount.setValue(Integer.valueOf(settings.getString("actionCircleFieldCount")));    // default

        autoSaveButton.setSelected(Boolean.parseBoolean(settings.getString("autoSaveButton")));
        intervalBox.setDisable(Boolean.parseBoolean(settings.getString("intervalBox")));
        autoSaveIntervalField.setText(settings.getString("autoSaveInterval"));

        hideLogButton.setSelected(Boolean.parseBoolean(settings.getString("hideLogButton")));

        colorPickerFighter.setValue(Color.web(settings.getString("colorFighter"))); //LIMEGREEN
        colorPickerAlly.setValue(Color.web(settings.getString("colorAlly"))); //MEDIUMBLUE
        colorPickerEnemy.setValue(Color.web(settings.getString("colorEnemy"))); //RED
    }
}
