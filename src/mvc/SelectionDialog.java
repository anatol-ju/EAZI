package mvc;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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
                return mergeFighter();
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
     * Initialize fields in DialogPane.
     * Use default values if parameter is <code>null</code>.
     * Otherwise use the given values from Fighter object.
     * @param fighter Object to use to initialize field's values.
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
                orientieren.setText(String.valueOf(fighter.getModOrientate()));
                waffeZiehen.setText(String.valueOf(fighter.getModDrawWeapon()));
                bogenLaden.setText(String.valueOf(fighter.getModLoadBow()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        name.selectAll();
        name.requestFocus();

    }

    /**
     * Initializing tooltips.
     */
    private void initTooltips() {
        modText.setTooltip(new Tooltip(rb.getString("tooltipModText")));
    }

    /**
     * Initialize and change values of ComboBox.
     */
    private void initBoxes(Fighter fighter) {
        // init
        List<String> list = Arrays.asList(rb.getString("fighter"), rb.getString("enemy"), rb.getString("ally"));
        ObservableList<String> initList = FXCollections.observableList(list);
        fighterSelection.setItems(initList);
        fighterSelection.getSelectionModel().select(0);
        if (fighter instanceof EnemyFighter) {
            fighterSelection.getSelectionModel().select(1);
        } else if (fighter instanceof AllyFighter) {
            fighterSelection.getSelectionModel().select(2);
        }
        // use listener to switch items in the ComboBox.
        fighterSelection.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> updateLevelString(newValue));
        updateLevelString(0);
    }

    /**
     * Listener for the list of items in ComboBox.
     * @param observable The Number object to be observed to switch view.
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
     * Filters invalid user data, especially INI < 0.
     * Prevents dialog window to be closed if invalid data is provided.
     * @param fighter Object to filter.
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
                new InfoDialog(rb.getString("errorIni")).showAndWait();
                event.consume();
            }
        });
    }

    /**
     * Merge data from dialog fields into Fighter object, taking into account the participant's affiliation.
     * @return Fighter object, can be <code>EnemyFighter</code> or <code>AllyFighter</code> and must be cast manually.
     */
    public Fighter mergeFighter() {

        Fighter mergedFighter = this.fighter;
        int selectedIndex = fighterSelection.getSelectionModel().getSelectedIndex();
        if (selectedIndex == 1) {
            mergedFighter = new EnemyFighter(this.fighter);
            ((EnemyFighter)mergedFighter).setLevel(levelSelection.getSelectionModel().getSelectedIndex());
        } else if (selectedIndex == 2) {
            mergedFighter = new AllyFighter(this.fighter);
            ((AllyFighter)mergedFighter).setLevel(levelSelection.getSelectionModel().getSelectedIndex());
        }

        try {
            mergedFighter.setPreviousIni(this.fighter.getIni());

            mergedFighter.setName(name.getText());
            mergedFighter.setIni(Integer.parseInt(ini.getText()));
            mergedFighter.setModAT(Integer.parseInt(attacke.getText()));
            mergedFighter.setModPosition(Integer.parseInt(position.getText()));
            mergedFighter.setModOrientate(Integer.parseInt(orientieren.getText()));
            mergedFighter.setModDrawWeapon(Integer.parseInt(waffeZiehen.getText()));
            mergedFighter.setModLoadBow(Integer.parseInt(bogenLaden.getText()));
        } catch (NumberFormatException e) {
            new TextDialog(rb.getString("errorNumber")).display();
        }

        return mergedFighter;
    }

    public void actionOk() {

        fighter = mergeFighter();

        if (fighter.getIni() <= 0) {
            new InfoDialog(rb.getString("errorIni")).showAndWait();
        }

        this.close();
    }

    /**
     * Calculates a random number for INI as of rolling 2 6-sided dice.
     * This produces a result between 2 and 12.
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
