package mvc;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController implements ListChangeListener {

    private Model model;
    private Controller controller;

    private List<FightersList> undoList;
    private int undoPointer;

    private boolean blockListChange = false;

    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem undo;
    @FXML
    private MenuItem redo;
    @FXML
    private CheckMenuItem useAssistant;

    @FXML
    private void initialize() {

        undoList = new LinkedList<>();
        undoPointer = -1;
        System.out.println("init: list " + undoList.size());

        undo.setDisable(true);
        redo.setDisable(true);

    }

    @Override
    public void onChanged(Change c) {
        if (!blockListChange) {
            this.undo.setDisable(undoList.isEmpty());
        }
    }

    /**
     * Diese Funktion muss von einem Controller ausgeführt werden, wenn die
     * Handlung in der undoList berücksichtigt werden soll.
     */
    public void action() {

        synchronized (this) {
            // Die Liste auf 10 Einträge begrenzen.
            if (undoList.size() > 10) {
                undoList.remove(0);
            }
            // Den Zeiger auf die Liste auf die Länge der Liste beschränken.
            if (undoPointer >= 10) {
                undoPointer = undoList.size() - 1;
            }

            System.out.println("< action() >");
            System.out.println("Pointer ist bei " + undoPointer);
            System.out.println("FightersList: \n" + controller.getFightersList());
            undoPointer = undoPointer + 1;
            FightersList copy = new FightersList(controller.fightersListProperty());

            // Wenn weitere Einträge in der Liste sind, werden diese gelöscht.
            if (undoPointer < undoList.size()) {
                List<FightersList> subList = undoList.subList(undoPointer, undoList.size());
                if (!subList.isEmpty()) {
                    subList.clear();
                }
            }

            System.out.println("copy: \n" + copy);

            undoList.add(copy);
            handleMenuItems();
            System.out.println("pointer: " + undoPointer);
            System.out.println("list size: " + undoList.size());

            System.out.println("</ action() >");
        }

    }

    public void resetAction() {
        ((Stage)this.menuBar.getScene().getWindow()).close();
        Platform.runLater(() ->
        {
            try {
                new Main().start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void saveWindowAction() {

    }

    public void loadWindowAction() {

    }

    public void closeAction() {
        ((Stage) this.menuBar.getScene().getWindow()).close();
    }

    public void undoAction() {
        synchronized (this) {
            System.out.println("< undoAction() >");
            System.out.println("Pointer ist bei " + undoPointer);
            System.out.println("FightersList: \n" + controller.getFightersList());
            if (undoPointer > 0) {
                undoPointer = undoPointer - 1;
                FightersList list = this.undoList.get(undoPointer);
                controller.updateModel(list);

                controller.getLogController().undoAction();
                System.out.println("Pointer ist bei " + undoPointer);
                System.out.println("neue Liste: \n" + undoList.get(undoPointer));
            }
            controller.getListController().getListEntries().refresh();
            controller.getCircleController().updateArcs();
            controller.getCircleController().placeTokens();

            handleMenuItems();
            System.out.println("</ undoAction() >");
        }
    }

    public void redoAction() {
        synchronized (this) {
            System.out.println("< redoAction() >");
            System.out.println("Pointer ist bei " + undoPointer);
            System.out.println("FightersList: \n" + controller.getFightersList());
            if (undoPointer < undoList.size() - 1) {
                undoPointer = undoPointer + 1;
                FightersList list = this.undoList.get(undoPointer);
                controller.updateModel(list);

                controller.getLogController().redoAction();
                System.out.println("Pointer ist bei " + undoPointer);
                System.out.println("neue Liste: \n" + undoList.get(undoPointer));
            }
            controller.getListController().getListEntries().refresh();
            controller.getCircleController().updateArcs();
            controller.getCircleController().placeTokens();

            handleMenuItems();
            System.out.println("</ redoAction() >");
        }
    }

    public void makeNewFighterAction(ActionEvent actionEvent) {
        controller.getListController().makeNewFighter(actionEvent);
    }

    public void editFighterAction(ActionEvent actionEvent) {
        controller.getListController().editFighter(actionEvent);
    }

    public void removeFighterAction(ActionEvent actionEvent) {
        controller.getListController().removeFighter(actionEvent);
    }

    public void saveImageAction() {
    }

    public void clearLogAction() {
    }

    public void saveFighterAction() {

    }

    public void loadFighterAction() {

    }

    public void aboutAction() {

    }

    private void handleMenuItems() {
        if (undoPointer <= 0) {
            this.undo.setDisable(true);
        } else {
            this.undo.setDisable(false);
        }
        if (undoPointer >= undoList.size() - 1) {
            this.redo.setDisable(true);
        } else {
            this.redo.setDisable(false);
        }
    }

    public void setRelations() {
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
