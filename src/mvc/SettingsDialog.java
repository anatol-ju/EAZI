package mvc;

import com.sun.javafx.css.Style;
import javafx.application.Platform;
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
    @FXML
    private Label textFieldInfoLabel;

    private final ButtonType reset, buttonOk;

    private final HashMap<String, Boolean> validatedFields = new HashMap<>();

    private final Configuration settings = Configuration.get();
    private final ResourceBundle locale = ResourceBundle.getBundle("locales.SettingsDialog", Locale.getDefault());
    private String defaultStyle;
    private boolean dialogShowed = false;

    private Controller controller;

    public SettingsDialog(Controller controller) {

        this.controller = controller;

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

        ResourceBundle res = ResourceBundle.getBundle("locales.SettingsDialog", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingsDialog.fxml"), res);
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
        setTitle(res.getString("settings"));

        // Platform.runLater(() -> { });
    }

    /**
     * Reset form values to previous settings.
     */
    private void resetForm() {

        actionCircleFieldCount.setValue(Integer.valueOf(settings.getProperty("actionCircleFieldCount")));    // default

        autoSaveButton.setSelected(Boolean.parseBoolean(settings.getProperty("autoSaveButton")));
        autoSaveIntervalField.setText(settings.getProperty("autoSaveInterval"));
        autosaveToggleButtonAction();

        hideLogButton.setSelected(Boolean.parseBoolean(settings.getProperty("hideLogButton")));
        hideLogToggleButtonAction();

        colorPickerFighter.setValue(Serializer.string2color(settings.getProperty("colorFighter")));
        colorPickerAlly.setValue(Serializer.string2color(settings.getProperty("colorAlly")));
        colorPickerEnemy.setValue(Serializer.string2color(settings.getProperty("colorEnemy")));

        durationSimpleActionField.setText(settings.getProperty("simpleActions"));
        durationComplexActionField.setText(settings.getProperty("simpleActionsSurcharge"));
        durationSimpleReactionField.setText(settings.getProperty("simpleReactions"));
        durationComplexReactionField.setText(settings.getProperty("simpleReactionsSurcharge"));
        durationFreeActionField.setText(settings.getProperty("freeActions"));
    }

    /**
     * Set form values to default.
     * A confirmation dialog is raised before applying all changes.
     */
    private void resetToDefault() {
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
        durationFreeActionField.setText("0");
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

        Configuration prop = Configuration.get();
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

        ConfirmDialog cd = new ConfirmDialog(locale.getString("resetConfirmationMessage"));
        Optional<Boolean> confirm = cd.showAndWait();
        if (confirm.isPresent() && confirm.get()) {
            Configuration.save(null);
            resetWindow();
        }

        return true;
    }

    public void resetWindow() {
        try {
            Serializer.quickSave(controller.getFightersList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ((Stage)this.getOwner().getScene().getWindow()).close();
        Platform.runLater(() ->
        {
            try {
                new Main().start(new Stage());
                controller.updateModel(Serializer.quickLoad());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Checks the textfield for consistency.
     * If anything is wrong, the field is highlighted and a popup is shown.
     * @param tf a TextField object to be validated.
     */
    private void validateTextField(TextField tf) {

        int value = 0;
        String strValue = tf.getText();

        if (strValue.matches("^[0-9]+$")) {
            value = Integer.parseInt(strValue);
        } else {
            tf.setStyle("-fx-text-box-border: red;");
            validatedFields.put(tf.getId(), false);
            return;
        }

        if (value < 0 | value > actionCircleFieldCount.getValue()) {
            tf.setStyle("-fx-text-box-border: red;");
            validatedFields.put(tf.getId(), false);
        } else {
            tf.setStyle(defaultStyle);
            validatedFields.put(tf.getId(), true);
        }
    }

    @FXML
    private void initialize() {

        savePath.setText(settings.getProperty("savePath"));

        defaultStyle = durationSimpleActionField.getStyle();

        actionCircleFieldCount.getItems().addAll(12,16,20,24);
        Tooltip tp = new Tooltip(locale.getString("actionFieldArcsTooltip"));
        tp.setWrapText(true);
        actionCircleFieldCount.setTooltip(tp);
        actionCircleFieldCount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectActionCircleFieldCount(newValue));

        // use defined functions to init
        resetForm();

        durationSimpleActionField.focusedProperty().addListener(observable -> validateTextField(durationSimpleActionField));
        durationComplexActionField.focusedProperty().addListener(observable -> validateTextField(durationComplexActionField));
        durationSimpleReactionField.focusedProperty().addListener(observable -> validateTextField(durationSimpleReactionField));
        durationComplexReactionField.focusedProperty().addListener(observable -> validateTextField(durationComplexReactionField));
        durationFreeActionField.focusedProperty().addListener(observable -> validateTextField(durationFreeActionField));
    }

    @FXML
    private void autosaveToggleButtonAction() {
        if(autoSaveButton.isSelected()) {
            autoSaveButton.setText(locale.getString("toggleOn"));
        } else {
            autoSaveButton.setText(locale.getString("toggleOff"));
        }
    }

    @FXML
    private void resetToDefaultAction() {
        ConfirmDialog cfd = new ConfirmDialog(locale.getString("confirmMessage"));
        Optional<Boolean> b = cfd.showAndWait();
        if(b.isPresent() && b.get()) {
            resetToDefault();
        }
    }

    @FXML
    private void hideLogToggleButtonAction() {
        if(hideLogButton.isSelected()) {
            hideLogButton.setText(locale.getString("toggleOn"));
        } else {
            hideLogButton.setText(locale.getString("toggleOff"));
        }
    }

    @FXML
    private void selectActionCircleFieldCount(int num) {
        boolean actionCircleFieldCountChanged = Integer.parseInt(settings.getProperty("actionCircleFieldCount")) != num;

        if (actionCircleFieldCountChanged && !dialogShowed) {
            dialogShowed = true;
            new InfoDialog(locale.getString("actionCircleFieldCountChangedMessage")).showAndWait();
        }
    }
}
