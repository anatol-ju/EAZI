package mvc;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Diese Klasse kann dazu verwendet werden Daten zu serialisieren und zu
 * deserialisieren. Dabei übernehmen die Methoden alle Funktionen, wie zum
 * Beispiel das Öffnen von Dialogen zum Auswählen der Speicherorte.
 */
public class Serializer {

    private String location = null;

    public Serializer() {
        try {
            this.location = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Object toSave) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Daten speichern");
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("EAZI GUI Daten", "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            saveXML(toSave, selectedFile.getPath());
        }
    }

    public DataContainer loadData() {
        DataContainer toLoad = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Daten laden");
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("EAZI GUI Daten", "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            toLoad = (DataContainer) loadXML(selectedFile.getPath());
        }
        return toLoad;
    }

    public void saveFighter(Fighter toSave) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Teilnehmer speichern");
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("EAZI GUI Daten", "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            saveXML(toSave, selectedFile.getPath());
        }
    }

    public Fighter loadFighter() {
        Fighter toLoad = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Teilnehmer laden");
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("EAZI GUI Daten", "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            toLoad = (Fighter) loadXML(selectedFile.getPath());
        }
        return toLoad;
    }

    private void saveXML(Object object, String file) {
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
            encoder.writeObject(object);
        } catch(IOException e) {
            new TextDialog("Speichern des Objektes fehlgeschlagen.").display();
        } finally {
            if (encoder != null)
                encoder.flush();
            encoder.close();
        } // end of try
    }

    private Object loadXML(String file) {
        Object loaded = new Object();
        XMLDecoder decoder = null;
        try {
            decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
            loaded = decoder.readObject();
        } catch(IOException e) {
            new TextDialog("Laden des Objektes fehlgeschlagen.").display();
        } finally {
            if (decoder != null)
                decoder.close();
        } // end of try
        return loaded;
    }

    /**
     * Write settings properties into a file.
     * Directory is "user.home" followed by "\eazi\filename".
     */
    public static void writeConfigFile(Properties properties) {
        try(FileOutputStream output = new FileOutputStream(ConfigContainer.getSettingsFilePath())) {
            if (properties != null) {
                properties.store(output, null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read properties file from directory.
     * @return Properties class containing data. If no file present returns null.
     */
    public static Properties readConfigFile() {
        Properties prp = new Properties();
        File file = new File(ConfigContainer.getSettingsFilePath());

        if (file.exists() && !file.isDirectory()) {
            try (FileInputStream input = new FileInputStream(file)) {
                prp.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return prp;
    }

    // TODO modify property utility function
    public static void editConfig(Properties properties, String key, String value) {

    }

}
