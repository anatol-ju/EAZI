package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SelectionModel;

import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class Controller implements Initializable, ListChangeListener {

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
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Das Model erstellen
        model = new Model(this);

        this.url = location;
        this.resourceBundle = resources;

        // load once and pass to sub-controllers
        this.settings = Serializer.readConfigFile();
        if (settings.isEmpty()) {
            settings = ConfigContainer.makeDefaultConfigFile();
        }

        fightersList = model.getFightersList();

        observableList = fightersList.getSortedList();
        observableList.addListener(this);

        // Das Model und den Controller bei allen anderen Controllern anmelden
        listController.setModel(model);
        listController.setController(this);

        actionsController.setModel(model);
        actionsController.setController(this);

        circleController.setModel(model);
        circleController.setController(this);

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
     * Diese Funktion sollte von Main aufgerufen werden um alle Verbindungen
     * zwischen den Controllern und anderen Komponenten herzustellen.
     * Das Ausführen dieser Funktion stellt sicher, dass alle Controller und
     * deren Komponenten bereits geladen und nicht leer sind. Insbesondere
     * können ChangeListener zugeordnet werden.
     */
    public void setRelations() {
        listController.setObservableList(observableList);

        SelectionModel selectionModel = listController.getSelectionModel();
        actionsController.setSelectionModel(selectionModel);

        observableList.addListener(circleController);
        observableList.addListener(menuController);

        listController.setRelations();
        actionsController.setRelations();
        circleController.setRelations();
        menuController.setRelations();
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

    public Model getModel() {
        return this.model;
    }

    public void setNewModel() {
        this.initialize(url, resourceBundle);
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
