package mvc;

import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MenuController implements PropertyChangeListener, ListChangeListener<Fighter> {

    private Model model;
    private Controller controller;

    private List<FightersList> undoList;
    private int undoPointer;

    private boolean blockListChange = false;

    private static File lastSave = null;

    private static ResourceBundle locales;

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

        locales = ResourceBundle.getBundle("locales.OtherDialog", Locale.getDefault());

        undoPointer = -1;

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
     * Always use this method to include undo/redo functionality.
     */
    public void action() {

        synchronized (this) {

            // limit to 10 entries
            if (undoList.size() > 10) {
                undoList.remove(0);
            }
            // limit pointer to list size
            if (undoPointer >= 10) {
                undoPointer = undoList.size() - 1;
            }

            System.out.println("< MenuController:action() >");
            System.out.println("Pointer ist bei " + undoPointer);
            System.out.println("FightersList: \n" + controller.getFightersList());
            undoPointer = undoPointer + 1;
            FightersList copy = new FightersList(controller.fightersListProperty());

            // delete entries that are out of range
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

            System.out.println("</ MenuController:action() >");
        }

    }

    /**
     * Reset the window by saving the current data to a temporary file using
     * {@code quickSave()} method from {@code Serializer} class.
     */
    public void resetAction() {
        // make a temporary save of the current data
        try {
            Serializer.quickSave(controller.getFightersList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        closeAction();
    }

    public void saveWindowAction() {
        DataContainer data = new DataContainer(controller.getFightersList());
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.get().getProperty("usedPath")));
        File file = fc.showSaveDialog(controller.getOwner());

        if (file != null) {
            try {
                String canonicalPath;
                canonicalPath = file.getCanonicalPath();
                Serializer.saveXML(data, canonicalPath);
                Configuration.saveProperty("usedPath", file.getParent(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void loadWindowAction() {
        DataContainer data = null;
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Configuration.get().getProperty("usedPath")));
        File file = fc.showOpenDialog(controller.getOwner());

        if (file != null) {
            try {
                String canonicalPath;
                canonicalPath = file.getCanonicalPath();
                data = (DataContainer)Serializer.loadXML(canonicalPath);
                Configuration.saveProperty("usedPath", file.getParent(), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // copy data into new FightersList
        if (data != null) {
            controller.updateModel(data.getData());
            System.out.println("Group loaded successfully!\n" + data.getData());
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
            fileChooser.setInitialDirectory(new File(Configuration.get().getProperty("userPath")));
        }

        //Show save file dialog
        lastSave = fileChooser.showSaveDialog(controller.getOwner());
        // save directory to configuration
        Configuration.saveProperty("userPath", lastSave.getParent(), null);

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
        ConfirmDialog cd = new ConfirmDialog(locales.getString("confirmDeleteSettings"));
        Optional<Boolean> b = cd.showAndWait();
        if (b.isPresent() && b.get()) {
            File file = Configuration.getSettingsFilePath().toFile();
            file.deleteOnExit();
            file = file.getParentFile();
            if (file.isDirectory()) {
                file.deleteOnExit();
            }
        }
    }

    public void saveFighterAction() {
        // TODO
    }

    public void loadFighterAction() {
        // TODO
    }

    public void aboutAction() {
        // TODO
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

    public void showSettingsDialogAction() {
        SettingsDialog sd = new SettingsDialog(controller);
        sd.initOwner(menuBar.getScene().getWindow());
        sd.initModality(Modality.WINDOW_MODAL);
        Optional<Boolean> booleanOptional = sd.showAndWait();
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setUndoList(List<FightersList> undoList) {
        this.undoList = undoList;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
