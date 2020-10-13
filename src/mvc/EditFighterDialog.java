package mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EditFighterDialog {

    private Stage primaryStage;
    private FXMLLoader fxmlLoader;
    private Fighter fighter;

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
    private Button buttonOk;
    @FXML
    private Button buttonExit;
    @FXML
    private Button buttonReset;

    public EditFighterDialog(Fighter fighter) {
        this.fighter = fighter;
    }

    public EditFighterDialog() {

    }

    public void initFields(Fighter fighter) {

        if (fighter == null) {
            name.setText("(leer)");
            ini.setText("1");
            attacke.setText("0");
            position.setText("0");
            orientieren.setText("0");
            waffeZiehen.setText("0");
            bogenLaden.setText("0");
        } else {
            name.setText(fighter.getName());
            ini.setText(String.valueOf(fighter.getIni()));
            attacke.setText(String.valueOf(fighter.getModAT()));
            position.setText(String.valueOf(fighter.getModPosition()));
            orientieren.setText(String.valueOf(fighter.getModOrientate()));
            waffeZiehen.setText(String.valueOf(fighter.getModDrawWeapon()));
            bogenLaden.setText(String.valueOf(fighter.getModLoadBow()));
        }

        name.selectAll();
        name.requestFocus();

    }

    /**
     * Zeigt den Dialog an.
     * Sollte ausgeführt werden, nachdem der Dialog erstellt wurde.
     */
    public void display(Fighter fighter) {

        this.fighter = fighter;

        fxmlLoader = new FXMLLoader(getClass().getResource("EditFighterDialog.fxml"));
        // Controller muss vor load definiert werden.
        fxmlLoader.setController(this);

        primaryStage = new Stage();

        Parent root = null;
        Scene scene = null;
        try {
            root = fxmlLoader.load();
            scene = new Scene(root);
            scene.setOnKeyPressed(this::handleKeyEvents);
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setScene(scene);
        primaryStage.initModality(Modality.WINDOW_MODAL);
        primaryStage.setTitle("Werte des Teilnehmers bearbeiten");
        primaryStage.setMinHeight(400);
        primaryStage.setMinWidth(300);
        primaryStage.setMaxHeight(800);
        primaryStage.setMaxWidth(600);

        initFields(this.fighter);

        primaryStage.showAndWait();

    }

    private void handleKeyEvents(KeyEvent event) {
        if(event.getCode() == KeyCode.ESCAPE) {
            actionExit();
        } else if(event.getCode() == KeyCode.ENTER) {
            actionOk();
        }
    }

    /**
     * Setzt die Werte des Teilnehmers auf die im Fenster eingegebenen.
     * @param fighter
     */
    public void parseData(Fighter fighter) {

        if(fighter == null) {
            this.fighter = new Fighter();
        }

        try {
            this.fighter.setName(name.getText());
            this.fighter.setPreviousIni(this.fighter.getIni());
            this.fighter.setIni(Integer.parseInt(ini.getText()));
            this.fighter.setModAT(Integer.parseInt(attacke.getText()));
            this.fighter.setModPosition(Integer.parseInt(position.getText()));
            this.fighter.setModOrientate(Integer.parseInt(orientieren.getText()));
            this.fighter.setModDrawWeapon(Integer.parseInt(waffeZiehen.getText()));
            this.fighter.setModLoadBow(Integer.parseInt(bogenLaden.getText()));
        } catch (NumberFormatException e) {
            new TextDialog("Fehler bei der Eingabe. \n Für INI und Modifikatoren numerische Werte benutzen.").display();
        }

    }

    public void actionOk() {

        parseData(fighter);

        if (fighter.getIni() <= 0) {
            new TextDialog("Die Initiative muss gleich '1' oder größer sein.").display();
        }

        primaryStage.close();
    }

    public void actionReset() {

        initFields(fighter);
    }

    public void actionExit() {

        primaryStage.close();
    }

    /**
     * Liefert einen Teilnehmer mit den im Dialog eingegebenen Werten.
     * Beim Wert <code>null</code> wurde vorher ein Teilnehmer an den Dialog
     * gegeben, der lediglich bearbeitet wird.
     * @return
     */
    public Fighter getFighter() {
        return fighter;
    }

    public void setFighter(Fighter fighter) {
        this.fighter = fighter;
        initFields(this.fighter);
    }
}
