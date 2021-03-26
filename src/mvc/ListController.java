package mvc;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ListController {

    private Controller controller;

    private SelectionModel<Fighter> selectionModel;
    private ReadOnlyIntegerProperty selectedIndex;
    private Fighter toReturn;
    private FightersList fightersList;

    @FXML
    private TitledPane titledPane;
    @FXML
    private GridPane listButtonsPane;
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

        ResourceBundle rb = ResourceBundle.getBundle("locales.ListController", Locale.getDefault());

        buttonNew.setTooltip(new Tooltip(rb.getString("buttonNewTooltip")));
        buttonEdit.setTooltip(new Tooltip(rb.getString("buttonEditTooltip")));
        buttonEdit.setDisable(true);
        buttonRemove.setTooltip(new Tooltip(rb.getString("buttonRemoveTooltip")));
        buttonRemove.setDisable(true);

        listView.setCellFactory(new FighterCellFactory());
        listView.setPlaceholder(makePlaceholder());
        //listView.setItems(sortedList);

        selectionModel = listView.getSelectionModel();
        selectedIndex = selectionModel.selectedIndexProperty();
        selectedIndex.addListener(this::changed);

        titledPane.setText(rb.getString("title"));
        buttonNew.setText(rb.getString("buttonNew"));
        buttonEdit.setText(rb.getString("buttonEdit"));
        buttonRemove.setText(rb.getString("buttonRemove"));
    }

    /**
     * Used by {@link ChangeListener} to enable/disable controls depending on selection.
     * @param observable the index of the selected list entry.
     * @param oldValue old selected index.
     * @param newValue currently selected index.
     */
    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

        int selectedIndex = newValue.intValue();
        if(selectedIndex == -1) {
            buttonEdit.setDisable(true);
            buttonRemove.setDisable(true);
        } else {
            buttonEdit.setDisable(false);
            buttonRemove.setDisable(false);
        }
    }

    /**
     * Defines a placeholder node to be shown on the View when the FightersList
     * is empty. Can be used as a short manual.
     * @return Node, in this case a {@link TextArea}. The text is taken
     * directly from the locales.
     */
    private Node makePlaceholder() {
        TextArea textArea = new TextArea();
        textArea.setText(ResourceBundle.getBundle("locales.ListController").getString("placeholderText"));
        textArea.setWrapText(true);

        return textArea;
    }

    /**
     * Creates a new Fighter object using the {@link SelectionDialog} dialog.
     * @param actionEvent Not used.
     */
    public void makeNewFighter(ActionEvent actionEvent) {
        try {
            SelectionDialog sd = new SelectionDialog();
            Optional<Fighter> returned = sd.showAndWait();
            if (returned.isPresent() && fightersList.addFighter(returned.get())) {
                controller.getMenuController().action();
                controller.getLogController().action(returned.get(), "join");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(listView.getItems());
        listView.refresh();
    }

    /**
     * Edit data from a {@link Fighter} object using the given instance.
     * If the fighters affiliation changes, the returned object is a new instance.
     * @param actionEvent Not used.
     */
    public void editFighter(ActionEvent actionEvent) {
        try {
            toReturn = listView.getSelectionModel().getSelectedItem();

            SelectionDialog sd = new SelectionDialog(toReturn);
            Optional<Fighter> returned = sd.showAndWait();
            if (returned.isPresent()) {
                if (returned.get() != toReturn) {
                    fightersList.removeFighter(toReturn);
                }
                fightersList.updateSubLists(returned.get());
                controller.getMenuController().action();
                controller.getLogController().action(returned.get(), "edited");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        listView.refresh();
        // kann ersetzt werden wenn Animationen funktionieren
        controller.getCircleController().updateArcs();
        controller.getCircleController().placeTokens();
    }

    /**
     * Remove the {@link Fighter} object from the list.
     * @param actionEvent Not used.
     */
    public void removeFighter(ActionEvent actionEvent) {
        Fighter selectedFighter;
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

        buttonEdit.setDisable(fightersList.getSortedList().isEmpty());
        buttonRemove.setDisable(fightersList.getSortedList().isEmpty());

        listView.refresh();
    }

    /**
     * Use this function to register any key event handlers to the given scene.
     * @param scene Scene that listens to the assigned keys.
     */
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

    public SelectionModel<Fighter> getSelectionModel() {
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
        this.listView.setItems(this.fightersList.getSortedList());
    }
}
