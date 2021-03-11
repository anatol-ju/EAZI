package mvc;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.stage.Window;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class Controller implements ListChangeListener {

    private Model model;

    private ObservableList<Fighter> observableList;
    private FightersList fightersList;

    private Properties settings;

    private Window owner;

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

        synchronized (this) {
            // make a new model
            model = new Model(this);

            // load once and pass to sub-controllers
            settings = Configuration.get();

            fightersList = model.getFightersList();

            wire();
        }
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

        observableList = FightersList.sortedList;
        observableList.addListener(this);
        observableList.addListener(circleController);
        observableList.addListener(menuController);

        // register model and main controller at all sub-controllers
        // to allow access between all of them
        listController.setModel(model);
        listController.setController(this);
        listController.setFightersList(fightersList);
        listController.getSelectionModel().selectedIndexProperty().addListener(actionsController);

        actionsController.setModel(model);
        actionsController.setController(this);
        actionsController.setSelectionModel(listController.getSelectionModel());
        actionsController.setSelectedIndex(listController.getSelectionModel().selectedIndexProperty());
        actionsController.setFightersList(fightersList);

        circleController.setModel(model);
        circleController.setController(this);
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

    /**
     * This method is required to implement undo and redo functionality.
     * It updates the <code>FightersList</code> with another instance
     * that had been saved previously in an undo-list.
     * @param fightersList The instance to be used. Overwrites the current one.
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

    public FightersList getFightersList() {
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

    public Properties getSettings() {
        return this.settings;
    }

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public Window getOwner() {
        return this.owner;
    }
}
