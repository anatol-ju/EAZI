package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Model {

    private Controller controller;

    private final FightersList fightersList;
    private final List<FightersList> undoList;

    public Model(Controller controller) {

        this.controller = controller;

        fightersList = new FightersList();
        controller.setFightersList(fightersList);

        undoList = new LinkedList<>();
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public FightersList getFightersList() {
        return fightersList;
    }

    public void updateFightersList(FightersList fightersList) {
        this.fightersList.update(fightersList);
    }

    public List<FightersList> getUndoList() {
        return undoList;
    }
}
