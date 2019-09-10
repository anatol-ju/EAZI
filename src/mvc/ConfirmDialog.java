package mvc;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class ConfirmDialog extends Dialog<Boolean> {

    public ConfirmDialog(String confirmMessage) {

        super();
        this.setTitle("Abfrage");
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);
        this.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Text text = new Text(confirmMessage);
        text.setTextAlignment(TextAlignment.CENTER);

        this.getDialogPane().setPadding(new Insets(10, 10, 10, 10));
        this.getDialogPane().setContent(text);

        this.setResult(false);
        this.setResultConverter(param -> {
            if (param.equals(ButtonType.OK)) {
                return true;
            } else {
                return false;
            }
        });

        this.showAndWait();

    }
}
