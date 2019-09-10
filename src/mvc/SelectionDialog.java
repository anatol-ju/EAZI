package mvc;

import javafx.beans.Observable;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.List;

public class SelectionDialog extends Dialog<Fighter> {

    @FXML
    private BorderPane panel;
    @FXML
    private TextField name;
    @FXML
    private TextField ini;
    @FXML
    private TextField attacke;
    @FXML
    private TextField position;
    @FXML
    private TextField orientieren;
    @FXML
    private TextField waffeZiehen;
    @FXML
    private TextField bogenLaden;
    @FXML
    private ComboBox<String> fighterSelection;
    @FXML
    private ComboBox<String> levelSelection;

    private List<String> levelString;

    public SelectionDialog() {
        super();

        this.getDialogPane().getButtonTypes().add(new ButtonType("Reset"));
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewOfSelection.fxml"));
        try {
            Parent root = loader.load();
            loader.setController(this);
            this.getDialogPane().setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("Werte des Teilnehmers bearbeiten");

        initBoxes();

        this.show();
    }

    private void initBoxes() {
        fighterSelection.getItems().addAll("Kämpfer", "Gegner", "Verbündeter");
        fighterSelection.getSelectionModel().select(0);
        fighterSelection.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> updateLevelString(newValue));
        updateLevelString(0);
    }

    private void updateLevelString(Number observable) {
        int obs = observable.intValue();
        final int selection = levelSelection.getSelectionModel().getSelectedIndex();
        levelSelection.getItems().clear();
        if (obs == 0) {
            levelSelection.getItems().addAll(
                    "Bauer", "Bürger", "Söldner", "Gardist", "Krieger", "Elitekämpfer", "Waffenmeister");
        } else if (obs == 1) {
            levelSelection.getItems().addAll(
                    "Halbstarker", "Taschendieb", "Raufbold", "Räuber", "Veteran", "Anführer", "Endgegner");
        } else if (obs == 2) {
            levelSelection.getItems().addAll(
                    "Niemand", "Helfer", "Unterstützung", "Profi", "Berufssoldat", "Elitekämpfer", "Waffenmeister");
        }
        levelSelection.getSelectionModel().select(selection);
    }
}
