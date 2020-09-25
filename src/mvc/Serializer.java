package mvc;

import javafx.beans.property.Property;
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
     * Write properties into a file.
     * Directory is "user.home" followed by "\eazi\filename".
     */
    private static void writeConfigFile() {
        try(FileOutputStream output = new FileOutputStream(
                // output directory and file
                System.getProperty("user.home") + "\\eazi\\userConfig.properties")) {
            Properties prp = new Properties();

            // fill properties
            // prp.setProperty("key","value");

            prp.store(output, null);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read properties file.
     * @return Properties class containing data
     */
    private static Properties readConfigFile() {
        // TODO get properties by key
        try(FileInputStream input = new FileInputStream(
                // input directory and file
                System.getProperty("user.home") + "\\eazi\\userConfig.properties")) {
            Properties prp = new Properties();
            prp.load(input);

            // get properties by key
            // prp.getProperty("key");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // TODO modify property utility function
    private static void editConfig(Property property, String key, String value) {

    }
}
