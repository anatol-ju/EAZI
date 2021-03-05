package mvc;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.*;

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
    private TextField durationSimpleActionField;
    @FXML
    private TextField durationSimpleReactionField;
    @FXML
    private TextField durationComplexActionField;
    @FXML
    private TextField durationComplexReactionField;
    @FXML
    private TextField durationFreeActionField;
    @FXML
    private ColorPicker colorPickerFighter;
    @FXML
    private ColorPicker colorPickerAlly;
    @FXML
    private ColorPicker colorPickerEnemy;

    private ButtonType reset, buttonOk;

    private HashMap<String, Boolean> validatedFields = new HashMap<>();

    private final ResourceBundle settings = ResourceBundle.getBundle("settings.settings");
    private final ResourceBundle locale = ResourceBundle.getBundle("locales.SettingsDialog", Locale.getDefault());

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
    }

    /**
     * Reset form values to previous settings.
     */
    private void resetForm() {

        actionCircleFieldCount.setValue(Integer.valueOf(settings.getString("actionCircleFieldCount")));    // default

        autoSaveButton.setSelected(Boolean.parseBoolean(settings.getString("autoSaveButton")));
        autoSaveIntervalField.setText(settings.getString("autoSaveInterval"));

        hideLogButton.setSelected(Boolean.parseBoolean(settings.getString("hideLogButton")));

        colorPickerFighter.setValue(Color.web(settings.getString("colorFighter"))); //LIMEGREEN
        colorPickerAlly.setValue(Color.web(settings.getString("colorAlly"))); //MEDIUMBLUE
        colorPickerEnemy.setValue(Color.web(settings.getString("colorEnemy"))); //RED

        durationSimpleActionField.setText(settings.getString("simpleActions"));
        durationComplexActionField.setText(settings.getString("simpleActionsSurcharge"));
        durationSimpleReactionField.setText(settings.getString("simpleReactions"));
        durationComplexReactionField.setText(settings.getString("simpleReactionsSurcharge"));
    }

    /**
     * Set form values to default.
     * A confirmation dialog is raised before applying all changes.
     */
    private void resetToDefault() {

        ConfirmDialog cfd = new ConfirmDialog(locale.getString("confirmMessage"));
        if(cfd.getResult()) {
            // general
            autoSaveButton.setSelected(true);
            autoSaveIntervalField.setText("2");

            // appearance
            hideLogButton.setSelected(false);

            colorPickerFighter.setValue(Color.LIMEGREEN);
            colorPickerAlly.setValue(Color.DODGERBLUE);
            colorPickerEnemy.setValue(Color.RED);

            // rules
            actionCircleFieldCount.setValue(12);
            durationSimpleActionField.setText("6");
            durationComplexActionField.setText("3");
            durationSimpleReactionField.setText("0");
            durationComplexReactionField.setText("3");
        }
    }

    /**
     * Used to validate form data and store them into preferences
     * before closing dialog manually.
     * @return true if properly validated.
     */
    private boolean validateAndStore() {

        for (Boolean value : validatedFields.values()) {
            if(!value) {
                new InfoDialog(locale.getString("validationFailedMessage")).showAndWait();
                return false;
            }
        }
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("settings.settings.properties"));
            prop.setProperty("autoSaveInterval", autoSaveIntervalField.getText());
            prop.setProperty("autoSaveButton", String.valueOf(autoSaveButton.isSelected()));
            prop.setProperty("hideLogButton", String.valueOf(hideLogButton.isSelected()));
            prop.setProperty("colorFighter", Serializer.color2string(colorPickerFighter.getValue()));
            prop.setProperty("colorAlly", Serializer.color2string(colorPickerAlly.getValue()));
            prop.setProperty("colorEnemy", Serializer.color2string(colorPickerEnemy.getValue()));
            prop.setProperty("actionCircleFieldCount", String.valueOf(actionCircleFieldCount.getValue()));
            prop.setProperty("simpleActions", durationSimpleActionField.getText());
            prop.setProperty("simpleActionsSurcharge", durationComplexActionField.getText());
            prop.setProperty("simpleReactions", durationSimpleReactionField.getText());
            prop.setProperty("simpleReactionsSurcharge", durationComplexReactionField.getText());
            prop.setProperty("freeActions", durationFreeActionField.getText());
            prop.store(new FileOutputStream("settings.settings.properties"), "updated");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Checks the textfield for consistency.
     * If anything is wrong, the field is highlighted and a popup is shown.
     * @param observable A TextField object to be validated.
     */
    private void validateTextField(Observable observable) {
        TextField obs;
        Tooltip tooltip = new Tooltip();

        try {
            obs = (TextField) observable;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }

        int value = 0;
        try {
            value = Integer.parseInt(obs.getText());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            tooltip.setText(locale.getString("numberFormatExceptionMessage"));
            obs.setTooltip(tooltip);
            tooltip.show(this.getOwner());
            validatedFields.put(obs.getId(), false);
        }

        String defaultStyle = obs.getStyle();
        if(obs.isFocused() && (value < 1 || value > actionCircleFieldCount.getValue())) {
            // change border color
            obs.setStyle("-fx-text-box-border: red;");
            // show tooltip with info
            tooltip.setText(locale.getString("textFieldValidationMessage"));
            tooltip.show(this.getOwner());
            validatedFields.put(obs.getId(), false);
        } else {
            // revert style
            obs.setStyle(defaultStyle);
            obs.setTooltip(null);
            validatedFields.put(obs.getId(), true);
        }
    }

    @FXML
    private void initialize() {

        savePath.setText(System.getProperty("user.home") + settings.getString("savePath"));

        actionCircleFieldCount.getItems().addAll(12,16,20,24);
        resetForm();

        durationSimpleActionField.focusedProperty().addListener(this::validateTextField);
        durationComplexActionField.focusedProperty().addListener(this::validateTextField);
        durationSimpleReactionField.focusedProperty().addListener(this::validateTextField);
        durationComplexReactionField.focusedProperty().addListener(this::validateTextField);
        durationFreeActionField.focusedProperty().addListener(this::validateTextField);
    }
}
