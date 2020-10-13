package mvc;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.ResourceBundle;

public class ListController implements ChangeListener {

    private Model model;
    private Controller controller;

    private ObservableList<Fighter> observableList;
    private SelectionModel<Fighter> selectionModel;
    private ReadOnlyIntegerProperty selectedIndex;
    private Fighter toReturn;
    private FightersList fightersList;

    @FXML
    private ListView<Fighter> listView;
    @FXML
    private Button buttonNew;
    @FXML
    private Button buttonEdit;
    @FXML
    private Button buttonRemove;

    @FXML
    private void initialize() {

        ResourceBundle rb = ResourceBundle.getBundle("locales.ListController");
        buttonNew.setTooltip(new Tooltip(rb.getString("buttonNewTooltip")));
        buttonEdit.setTooltip(new Tooltip(rb.getString("buttonEditTooltip")));
        buttonEdit.setDisable(true);
        buttonRemove.setTooltip(new Tooltip(rb.getString("buttonRemoveTooltip")));
        buttonRemove.setDisable(true);

        listView.setCellFactory(new FighterCellFactory());
        listView.setPlaceholder(makePlaceholder());
        listView.setItems(observableList);

        selectionModel = listView.getSelectionModel();
        selectedIndex = selectionModel.selectedIndexProperty();
        selectedIndex.addListener(this);
    }

    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {

        int selectedIndex = (int) newValue;
        if(selectedIndex == -1) {
            buttonEdit.setDisable(true);
            buttonRemove.setDisable(true);
        } else {
            buttonEdit.setDisable(false);
            buttonRemove.setDisable(false);
        }

    }

    /**
     * Ermöglicht Kommunikation zwischen diesem Controller und anderen
     * Komponenten, die nicht in <code>initialize()</code> verfügbar sind.
     */
    public void setRelations() {

        fightersList = controller.fightersListProperty();
        listView.setItems(observableList);
    }

    /**
     * Definiert eine Anzeige für den Listenbereich, wenn dieser leer ist.
     * Sie kann dazu verwendet werden Informationen zur Bedienung zu liefern.
     * @return Node
     */
    private Node makePlaceholder() {
        TextArea textArea = new TextArea();
        textArea.setText(ResourceBundle.getBundle("locales.ListController").getString("placeholderText"));
        textArea.setWrapText(true);

        return textArea;
    }

    public void makeNewFighter(ActionEvent actionEvent) {
        try {
            /*
            EditFighterDialog efd = new EditFighterDialog();
            efd.display(null);
            if (fightersList.addFighter(efd.getFighter())) {
                controller.getMenuController().action();
                controller.getLogController().action(efd.getFighter(), "join");
            }

             */
            SelectionDialog sd = new SelectionDialog();
            Optional<Fighter> returned = sd.showAndWait();
            if (returned.isPresent() && fightersList.addFighter(returned.get())) {
                controller.getMenuController().action();
                controller.getLogController().action(returned.get(), "join");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.refresh();

    }

    public void editFighter(ActionEvent actionEvent) {
        try {
            toReturn = listView.getSelectionModel().getSelectedItem();

            SelectionDialog sd = new SelectionDialog(toReturn);
            Optional<Fighter> returned = sd.showAndWait();
            if (returned.isPresent()) {
                fightersList.updateSubLists(returned.get());
                controller.getMenuController().action();
                controller.getLogController().action(returned.get(), "bearbeitet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.refresh();
        // kann ersetzt werden wenn Animationen funktionieren
        controller.getCircleController().updateArcs();
        controller.getCircleController().placeTokens();
    }

    public void removeFighter(ActionEvent actionEvent) {
        Fighter selectedFighter;
        ObservableList<Fighter> observableList = model.getObservableList();
        try {
            selectedFighter = listView.getSelectionModel().getSelectedItem();
            if(selectedFighter != null) {
                fightersList.removeFighter(selectedFighter);
                controller.getMenuController().action();
                controller.getLogController().action(selectedFighter, "leave");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        buttonEdit.setDisable(observableList.isEmpty());
        buttonRemove.setDisable(observableList.isEmpty());

        listView.refresh();
    }

    private Stage makeModalityStage() {
        Stage stage = new Stage();
        stage.initOwner(buttonNew.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        return stage;
    }

    public void setKeyEventHandlers(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, this::setHandler);
    }

    private void setHandler(KeyEvent event) {
        if (event.isControlDown()) {
            if (event.getCode().equals(KeyCode.N)) {
                makeNewFighter(new ActionEvent());
            } else if (event.getCode().equals(KeyCode.E)) {
                editFighter(new ActionEvent());
            } else if (event.getCode().equals(KeyCode.D)) {
                removeFighter(new ActionEvent());
            }
        }
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setObservableList(ObservableList observableList) {
        this.observableList = observableList;
    }

    public SelectionModel getSelectionModel() {
        return selectionModel;
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public ListView<Fighter> getListView() {
        return listView;
    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
    }

    public void setToReturn(Fighter toReturn) {
        this.toReturn = toReturn;
    }

    public Fighter getToReturn() {
        return toReturn;
    }
}
