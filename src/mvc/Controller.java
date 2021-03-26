package mvc;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;
import javafx.stage.Window;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;

public class Controller implements PropertyChangeListener {

    private Model model;

    private ObservableList<Fighter> sortedFightersList;
    private FightersList fightersList;
    private List<FightersList> undoList;
    private int undoPointer;

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
     * Initialize the Model and FightersList instance.
     */
    @FXML
    private void initialize() {

        synchronized (this) {
            // make a new model
            model = new Model(this);

            // try to find a temporary save
            // if not available, use list from model
            try {
                fightersList = Serializer.quickLoad();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (fightersList != null) {
                model.updateFightersList(fightersList);
            } else {
                fightersList = model.getFightersList();
            }

            wire();
        }
    }

    /**
     * Wires all connections between sub-controllers and registers listeners
     */
    private void wire() {
        // register model and main controller at all sub-controllers
        // to allow access between all of them
        listController.setController(this);

        SelectionModel<Fighter> selectionModel = listController.getSelectionModel();
        actionsController.bindSelectedIndexProperty(selectionModel.selectedIndexProperty());

        actionsController.setModel(model);
        actionsController.setController(this);

        circleController.setModel(model);
        circleController.setController(this);

        logController.setModel(model);
        logController.setController(this);

        menuController.setModel(model);
        menuController.setController(this);
        menuController.setUndoList(model.getUndoList());
    }

    /**
     * Use this method to update the {@link FightersList} instance.
     * This pushes the request to the model directly and updates all required
     * instances and listeners.<br/>
     * <b>Warning:</b> Do not use {@link #setFightersList(FightersList)}!
     * @param fightersList The instance to be used. Overwrites the current one.
     */
    public void updateModel(FightersList fightersList) {
        model.updateFightersList(fightersList);
    }

    /**
     * Reports changes in {@link FightersList} instance to associated observers.
     * @param evt the event, fired by the observable.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.fightersList = (FightersList) evt.getNewValue();
        //this.fightersList.updateSortedList();

        this.listController.setFightersList(this.fightersList);
        this.actionsController.setFightersList(this.fightersList);
        this.circleController.setFightersList(this.fightersList);
    }

    /**
     * Manage key events.
     * @param scene The scene that events are to be applied to.
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

    public void setOwner(Window owner) {
        this.owner = owner;
    }

    public Window getOwner() {
        return this.owner;
    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
        // wiring
        listController.setFightersList(fightersList);
        actionsController.setFightersList(fightersList);
        circleController.setFightersList(fightersList);
    }

    public List<FightersList> getUndoList() {
        return undoList;
    }

    public void setUndoList(List<FightersList> undoList) {
        this.undoList = undoList;

    }
}
