package mvc;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class MenuController implements ListChangeListener {

    private Model model;
    private Controller controller;

    private List<FightersList> undoList;
    private int undoPointer;

    private boolean blockListChange = false;

    private static File lastSave = null;

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
        Serializer serializer = new Serializer();
        DataContainer data = new DataContainer();
        FightersList fl = controller.getFightersList();

        // copy the FightersList instance into common List
        List<List<Fighter>> list = new ArrayList<>(fl.size());

        for (int index = 0; index < fl.size(); index++) {
            List<Fighter> sublist = new ArrayList<>(fl.get(index).size());
            list.add(index, sublist);
            for (int ind = 0; ind < fl.get(index).size(); ind++) {
                sublist.add(ind, fl.get(index).get(ind));
            }
        }

        data.setFighterList(list);
        data.setFieldIndex(fl.getSubListIndex());

        File file = null;
        FileChooser fc = new FileChooser();
        file = fc.showSaveDialog(controller.getOwner());

        if (file != null) {
            try {
                String canonicalPath;
                canonicalPath = file.getCanonicalPath();
                serializer.saveXML(data, canonicalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWindowAction() {
        Serializer serializer = new Serializer();
        DataContainer data = null;

        File file = null;
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(controller.getOwner());

        if (file != null) {
            try {
                String canonicalPath;
                canonicalPath = file.getCanonicalPath();
                data = (DataContainer)serializer.loadXML(canonicalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // copy data into new FightersList
        if (data != null) {
            List<List<Fighter>> list = data.getFighterList();
            FightersList fl = new FightersList(list.size());

            for (int index = 0; index < list.size(); index++) {
                for (int ind = 0; ind < list.get(index).size(); ind++) {
                    fl.get(index).add(ind, list.get(index).get(ind));
                }
            }

            // update the model
            fl.setSubListIndex(data.getFieldIndex());
            controller.updateModel(fl);
        }
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
            controller.getListController().getListView().refresh();
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
            controller.getListController().getListView().refresh();
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
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // start FileChooser at last used directory
        if (lastSave != null) {
            fileChooser.setInitialDirectory(new File(lastSave.getParent()));
        }

        //Show save file dialog
        lastSave = fileChooser.showSaveDialog(controller.getOwner());

        if(lastSave != null){
            try {
                WritableImage writableImage = controller.getCircleController().getImage();
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", lastSave);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void clearLogAction() {
        controller.getLogController().clearLog(0);
    }

    /**
     * Used to delete the settings file and the folder that contains it.
     */
    public void deleteSettingsFileAction() {
        ConfirmDialog cd = new ConfirmDialog("Are you sure to delete the configurations\n" +
                "file from your computer?");
        Optional<Boolean> b = cd.showAndWait();
        if (b.isPresent() && b.get()) {
            File file = ConfigContainer.getSettingsFilePath().toFile();
            file.deleteOnExit();
            file = file.getParentFile();
            if (file.isDirectory()) {
                file.deleteOnExit();
            }
            new InfoDialog("The file will be deleted after the application is closed.").showAndWait();
        }
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
