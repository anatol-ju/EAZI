package mvc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;

public class FighterPanel extends ListCell<Fighter> {

    private ListController listController;

    @FXML
    private GridPane gridPane;
    @FXML
    private Label nameLabel;
    @FXML
    private Label iniLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Pane leftPane;
    @FXML
    private Pane upperPane;
    @FXML
    private Pane lowerPane;
    @FXML
    private Pane rightPane;

    public FighterPanel() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FighterPanel.fxml"));

        try {
            fxmlLoader.setController(this);
            gridPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void updateItem(Fighter item, boolean empty) {
        super.updateItem(item, empty);

        if(item != null && !empty) {
            nameLabel.setText(item.getName());
            statusLabel.setText("");
            iniLabel.setText("INI: " + String.valueOf(item.getIni()));

            Color color;
            color = item.getColor();
            leftPane.setBackground(
                    new Background(
                            new BackgroundFill(
                                    color,
                                    CornerRadii.EMPTY,
                                    new Insets(0.0, 0.0, 0.0, 0.0))));

            setGraphic(gridPane);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } else {
            setText(null);
            setGraphic(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    public void setListController(ListController listController) {
        this.listController = listController;
    }
}
