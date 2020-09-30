package mvc;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.Locale;
import java.util.ResourceBundle;

public class InfoDialog extends Dialog {

    public InfoDialog(String infoMessage) {

        super();
        ResourceBundle rb = ResourceBundle.getBundle("OtherDialog", Locale.getDefault());
        this.setTitle(rb.getString("infoTitle"));
        this.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Text text = new Text(infoMessage);
        text.setTextAlignment(TextAlignment.CENTER);

        this.getDialogPane().setPadding(new Insets(10, 10, 10, 10));
        this.getDialogPane().setContent(text);

        this.showAndWait();

    }
}
