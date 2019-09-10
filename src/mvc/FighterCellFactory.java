package mvc;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class FighterCellFactory implements Callback<ListView<Fighter>, ListCell<Fighter>> {

    private ListController listController;

    @Override
    public ListCell<Fighter> call(ListView<Fighter> param) {
        FighterPanel fighterPanel = new FighterPanel();
        fighterPanel.setListController(listController);
        return fighterPanel;
    }

    public ListController getListController() {
        return listController;
    }

    public void setListController(ListController listController) {
        this.listController = listController;
    }
}
