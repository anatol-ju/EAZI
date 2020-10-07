package mvc;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.*;

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
    @FXML
    private Label modText;
    @FXML
    private Button randomIni;

    private Fighter fighter;
    private List<String> levelString;
    private ButtonType reset, buttonOk;
    ResourceBundle rb = null;

    public SelectionDialog() {
        this(new Fighter());
    }

    public SelectionDialog(Fighter fighter) {
        super();

        if (fighter == null) {
            return;
        } else {
            this.fighter = fighter;
        }

        reset = new ButtonType("Reset");
        buttonOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(reset);
        this.getDialogPane().getButtonTypes().add(buttonOk);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        rb = ResourceBundle.getBundle("locales.SelectionDialog", Locale.getDefault());

        // Liefert einen gültigen Rückgabewert (Fighter) oder Null wenn keine Änderungen erfolgt sind.
        this.setResultConverter(param -> {
            if (param == buttonOk) {
                return parseData();
            }
            return null;
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewOfSelection.fxml"));
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
        setTitle(rb.getString("selectionDialogTitle"));

        Platform.runLater(() -> {
            initFields(this.fighter);
            initBoxes(this.fighter);
            initTooltips();
            setButtonEventFilter(this.fighter);
        });

        //this.showAndWait();
    }

    /**
     * Initialisiert die Felder des DialogPane.
     * Wenn der Parameter fighter <code>null</code> ist, werden Standardwerte gesetzt.
     * Wird ein gültiges Fighter-Objekt erkannt, werden dessen Werte ausgelesen und gesetzt.
     * @param fighter
     */
    public void initFields(Fighter fighter) {

        if (fighter == null) {
            name.setText(rb.getString("emptyName"));
            ini.setText("1");
            attacke.setText("0");
            position.setText("0");
            orientieren.setText("0");
            waffeZiehen.setText("0");
            bogenLaden.setText("0");
        } else {
            try {
                name.setText(fighter.getName());
                ini.setText(String.valueOf(fighter.getIni()));
                attacke.setText(String.valueOf(fighter.getModAT()));
                position.setText(String.valueOf(fighter.getModPosition()));
                orientieren.setText(String.valueOf(fighter.getModOrientieren()));
                waffeZiehen.setText(String.valueOf(fighter.getModZiehen()));
                bogenLaden.setText(String.valueOf(fighter.getModLaden()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        name.selectAll();
        name.requestFocus();

    }

    /**
     * Initialisiert eventuell verwendete Tooltips.
     */
    private void initTooltips() {
        modText.setTooltip(new Tooltip(rb.getString("tooltipModText")));
    }

    /**
     * Initialisiert und Ändert den Inhalt der ComboBoxes abhängig von der Auswahl der Art des Teilnehmers.
     */
    private void initBoxes(Fighter fighter) {
        // Initialisierung
        List<String> list = Arrays.asList(rb.getString("fighter"), rb.getString("enemy"), rb.getString("ally"));
        ObservableList<String> initList = FXCollections.observableList(list);
        fighterSelection.setItems(initList);
        fighterSelection.getSelectionModel().select(0);
        if (fighter instanceof EnemyFighter) {
            fighterSelection.getSelectionModel().select(1);
        } else if (fighter instanceof AllyFighter) {
            fighterSelection.getSelectionModel().select(2);
        }
        // Listener überwacht die Auswahl und ändert den Text der ComboBox
        fighterSelection.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> updateLevelString(newValue));
        updateLevelString(0);
    }

    /**
     * Listener für die Auswahl des Teilnehmers.
     * Der angezeigte Text ist für die Orientierung, ändert aber nichts an der Mechanik.
     * @param observable
     */
    private void updateLevelString(Number observable) {
        int obs = observable.intValue();
        int selection = levelSelection.getSelectionModel().getSelectedIndex();
        if (selection < 0) {
            selection = 0;
        }

        if (obs == 0) {
            String[] initStrings = {rb.getString("hero")};
            ObservableList<String> initList = FXCollections.observableList(Arrays.asList(initStrings));
            levelSelection.setItems(initList);
        } else if (obs == 1) {
            String[] initStrings = {rb.getString("enemy0"), rb.getString("enemy1"), rb.getString("enemy2"),
                    rb.getString("enemy3"), rb.getString("enemy4"),
                    rb.getString("enemy5"), rb.getString("enemy6")};
            ObservableList<String> initList = FXCollections.observableList(Arrays.asList(initStrings));
            levelSelection.setItems(initList);
        } else if (obs == 2) {
            String[] initStrings = {rb.getString("ally0"), rb.getString("ally1"), rb.getString("ally2"),
                    rb.getString("ally3"), rb.getString("ally4"),
                    rb.getString("ally5"), rb.getString("ally6")};
            ObservableList<String> initList = FXCollections.observableList(Arrays.asList(initStrings));
            levelSelection.setItems(initList);
        }
        levelSelection.getSelectionModel().select(selection);
    }

    /**
     * Fängt ungültige Eingaben (INI < 0) ab und verhindert dass in solchen Fällen das Dialogfenster geschlossen wird.
     */
    private void setButtonEventFilter(Fighter fighter) {

        ((Button) this.getDialogPane().lookupButton(this.reset)).setDefaultButton(false);
        ((Button) this.getDialogPane().lookupButton(this.reset)).setCancelButton(false);
        this.getDialogPane().lookupButton(this.reset).addEventFilter(ActionEvent.ACTION, event -> {
            initFields(fighter);
            event.consume();
        });

        Button buttonOk = (Button) this.getDialogPane().lookupButton(this.buttonOk);
        buttonOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (fighter.getIni() <= 0) {
                new TextDialog(rb.getString("errorIni")).display();
                event.consume();
            }
        });
    }

    /**
     * Setzt die Werte des Teilnehmers auf die im Fenster eingegebenen.
     * @return Fighter
     */
    public Fighter parseData() {

        Fighter parsedFighter = this.fighter;
        int selectedIndex = fighterSelection.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 1) {
            parsedFighter = new EnemyFighter(this.fighter);
            ((EnemyFighter)parsedFighter).setLevel(levelSelection.getSelectionModel().getSelectedIndex());
        } else if (selectedIndex == 2) {
            parsedFighter = new AllyFighter(this.fighter);
            ((AllyFighter)parsedFighter).setLevel(levelSelection.getSelectionModel().getSelectedIndex());
        }

        try {
            parsedFighter.setPreviousIni(this.fighter.getIni());

            parsedFighter.setName(name.getText());
            parsedFighter.setIni(Integer.parseInt(ini.getText()));
            parsedFighter.setModAT(Integer.parseInt(attacke.getText()));
            parsedFighter.setModPosition(Integer.parseInt(position.getText()));
            parsedFighter.setModOrientieren(Integer.parseInt(orientieren.getText()));
            parsedFighter.setModZiehen(Integer.parseInt(waffeZiehen.getText()));
            parsedFighter.setModLaden(Integer.parseInt(bogenLaden.getText()));
        } catch (NumberFormatException e) {
            new TextDialog(rb.getString("errorNumber")).display();
        }

        return parsedFighter;
    }

    public void actionOk() {

        fighter = parseData();

        if (fighter.getIni() <= 0) {
            new TextDialog(rb.getString("errorIni")).display();
        }

        this.close();
    }

    /**
     * Calculates a random number for INI as of rolling 2 6-sided dice.
     */
    @FXML
    public void calcRandomIni() {
        int randomInt = new Random().nextInt(6) + new Random().nextInt(6) + 2;
        this.ini.setText(String.valueOf(randomInt));
    }

    public void actionReset() {
        initFields(fighter);
    }

    public void actionExit() {
        this.close();
    }
}
