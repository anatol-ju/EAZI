package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements ListChangeListener {

    private Model model;

    private URL url;
    private ResourceBundle resourceBundle;
    private Scene scene;

    private ObservableList<Fighter> observableList;
    private FightersList fightersList;
    private Properties settings;

    @FXML
    private ListController listController;
    @FXML
    private ActionsController actionsController;
    @FXML
    private CircleController circleController;
    @FXML
    private LogController logController;
    @FXML
    private MenuController menuController;

    /**
     * Initialisiert das Model und andere Attribute.
     */
    @FXML
    private void initialize() {

        // make a new model
        model = new Model(this);

        // load once and pass to sub-controllers
        this.settings = Serializer.readConfigFile();

        fightersList = model.getFightersList();

        wire();
    }

    /**
     * Wird von ListChangeListener verwendet.
     * Debug only.
     * @param c
     */
    @Override
    public void onChanged(Change c) {
        c.next();
        if(c.wasPermutated()) {
            System.out.println("permutated");
        } else if(c.wasAdded()) {
            System.out.println("added");
        } else if(c.wasRemoved()) {
            System.out.println("removed");
        } else if(c.wasUpdated()) {
            System.out.println("updated");
        }
    }

    /**
     * Wenn Änderungen an der Liste gemacht werden, werden die Änderungen an
     * die Controller der einzelnen Views weiter gereicht.
     */
    private void onChanging() {
    }

    /**
     * Wires all connections between sub-controllers and registers listeners
     */
    private void wire() {

        observableList = fightersList.getSortedList();
        observableList.addListener(this);
        observableList.addListener(circleController);
        observableList.addListener(menuController);

        // register model and main controller at all sub-controllers
        // to allow access between all of them
        listController.setModel(model);
        listController.setController(this);
        listController.setFightersList(fightersList);
        listController.setObservableList(observableList);
        listController.getSelectionModel().selectedIndexProperty().addListener(actionsController);

        actionsController.setModel(model);
        actionsController.setController(this);
        actionsController.setSelectionModel(listController.getSelectionModel());
        actionsController.setSelectedIndex(listController.getSelectionModel().selectedIndexProperty());
        actionsController.setFightersList(fightersList);

        circleController.setModel(model);
        circleController.setController(this);
        circleController.setObservableList(observableList);
        circleController.setFightersList(fightersList);

        logController.setModel(model);
        logController.setController(this);

        menuController.setModel(model);
        menuController.setController(this);

        // Alle Controller im Model anmelden für späteren Zugriff
        model.setListController(listController);
        model.setActionsController(actionsController);
        model.setCircleController(circleController);
        model.setLogController(logController);
    }

    public void updateObservableList() {
        fightersList.updateSortedList();
        observableList.setAll(fightersList.getSortedList());
    }

    /**
     * Diese Funktion aktualisiert das Model indem es eine neue FightersList
     * lädt und alle abgeleiteten Objekte anpasst.
     * @param fightersList
     */
    public void updateModel(FightersList fightersList) {
        this.fightersList = fightersList;

        this.listController.setFightersList(fightersList);
        this.actionsController.setFightersList(fightersList);
        this.circleController.setFightersList(fightersList);

        this.fightersList.updateSortedList();
    }

    /**
     * Diese Funktion wird von <code>Main</code> aufgerufen und leitet
     * die Scene weiter um KeyEvents zu definieren.
     * @param scene
     */
    public void setOnKeyPressed(Scene scene) {
        this.actionsController.setKeyEventHandlers(scene);
        this.listController.setKeyEventHandlers(scene);
    }

    public ListController getListController() {
        return this.listController;
    }

    public ActionsController getActionsController() {
        return this.actionsController;
    }

    public CircleController getCircleController() {
        return this.circleController;
    }

    public LogController getLogController() {
        return this.logController;
    }

    public MenuController getMenuController() {
        return this.menuController;
    }

    public List<SimpleListProperty<Fighter>> getFightersList() {
        return fightersList;
    }

    public FightersList fightersListProperty() {
        return fightersList;
    }

    public ObservableList getObservableList() {
        return this.observableList;
    }

    public void setObservableList(ObservableList<Fighter> observableList) {
        this.observableList = observableList;
    }
}
