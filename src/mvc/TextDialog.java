package mvc;

import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TextDialog {

    private String dialogText;
    private Button button;
    private boolean returnValue = false;

    private Stage stage;

    public TextDialog(String dialogText) {
        this.dialogText = dialogText;
    }

    public void display() {
        stage = new Stage();

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);

        button = new Button("OK");
        button.setOnAction(this::handle);
        gridPane.add(button, 0, 1);

        Label label = new Label(dialogText);
        gridPane.add(label, 0, 0);

        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setHalignment(button, HPos.CENTER);

        stage.setScene(new Scene(gridPane));
        stage.show();
    }

    private void handle(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(button)) {
            returnValue = true;
        } else {
            returnValue = false;
        }
        stage.close();
    }

    public boolean getReturnValue() {
        return returnValue;
    }
}
