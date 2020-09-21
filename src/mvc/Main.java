package mvc;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle resources = getResources();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"), resources);

        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle(resources.getString("mainTitle"));
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        Controller controller = loader.getController();
        controller.setOnKeyPressed(scene);
        controller.setRelations();

        primaryStage.show();
    }

    /**
     * Returns a RecourceBundle containing a Locale to switch between languages.
     * English is default unless another language is used and provided by properties.
     * @return ResourceBundle
     */
    private ResourceBundle getResources() {
        Locale sysDefaultLocale = Locale.getDefault();
        System.out.println(sysDefaultLocale);
        ResourceBundle rb = ResourceBundle.getBundle("locales.Languages", Locale.ENGLISH);

        if(sysDefaultLocale.equals(Locale.GERMANY)) {
            rb = ResourceBundle.getBundle("locales.Languages", Locale.GERMANY);
        }

        return rb;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
