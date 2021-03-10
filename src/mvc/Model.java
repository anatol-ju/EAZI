package mvc;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class Model {

    private Controller controller;
    private ListController listController;
    private ActionsController actionsController;
    private CircleController circleController;
    private LogController logController;

    private FightersList fightersList;
    private ObservableList<Fighter> observableList;

    public Model(Controller controller) {

        this.controller = controller;

        fightersList = new FightersList();

        /*
        list = new ListOfFighters();
        observableList = list.observableListProperty();
        observedSize = list.observableSizeProperty();
        */
    }

    public void saveFullData() {
        List<List<Fighter>> saveList = new ArrayList<>(12);
        for (int index = 0; index <= 11; index++) {
            List<Fighter> baseList = makeSerializable(fightersList.get(index));
            saveList.add(index, baseList);
        }
        DataContainer dataContainer = new DataContainer(saveList, fightersList.getSubListIndex(), fightersList.getMaxIni());
        Serializer serializer = new Serializer();
        serializer.saveData(dataContainer);
    }

    public void loadFullData() {
        FightersList fightersList = new FightersList();
        Serializer serializer = new Serializer();
        DataContainer dataContainer = serializer.loadData();
        List<List<Fighter>> loadList = dataContainer.getFighterList();
        for (int index = 0; index < loadList.size(); index++) {
            SimpleListProperty<Fighter> baseList = new SimpleListProperty<>(makeObservable(loadList.get(index)));
            fightersList.set(index, baseList);
        }
        fightersList.setSubListIndex(dataContainer.getFieldIndex());
        fightersList.setMaxIni(dataContainer.getMaxIni());
        //controller.updateModel(fightersList);
    }

    public void saveFighter(Fighter fighter) {
        Serializer serializer = new Serializer();
        serializer.saveFighter(fighter);
    }

    public void loadFighter() {
        Serializer serializer = new Serializer();
        Fighter fighter = serializer.loadFighter();
        fightersList.addFighter(fighter);
    }

    public List<Fighter> makeSerializable(ObservableList<Fighter> observableList) {
        return new LinkedList<Fighter>(observableList);
    }

    public ObservableList<Fighter> makeObservable(List<Fighter> serializableList) {
        return FXCollections.observableList(serializableList);
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public ListController getListController() {
        return listController;
    }

    public void setListController(ListController listController) {
        this.listController = listController;
    }

    public ActionsController getActionsController() {
        return actionsController;
    }

    public void setActionsController(ActionsController actionsController) {
        this.actionsController = actionsController;
    }

    public CircleController getCircleController() {
        return circleController;
    }

    public void setCircleController(CircleController circleController) {
        this.circleController = circleController;
    }

    public LogController getLogController() {
        return logController;
    }

    public void setLogController(LogController logController) {
        this.logController = logController;
    }

    public ObservableList<Fighter> getObservableList() {
        return observableList;
    }

    public FightersList getFightersList() {
        return fightersList;
    }

    public void setFightersList(FightersList fightersList) {
        this.fightersList = fightersList;
    }
}
