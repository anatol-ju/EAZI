package mvc;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * This class contains methods to serialize objects.
 * It includes support for <code>Fighter</code>, <code>DataContainer</code> and
 * any <code>Object</code>.
 */
public class Serializer {

    private String location = null;
    private static final ResourceBundle rb = ResourceBundle.getBundle("locales.Serializer", Locale.getDefault());

    public Serializer() {
        try {
            this.location = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void saveData(Object toSave) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("titleSave"));
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(rb.getString("extFilter"), "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            saveXML(toSave, selectedFile.getPath());
        }
    }

    public DataContainer loadData() {
        DataContainer toLoad = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("titleLoad"));
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(rb.getString("extFilter"), "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            toLoad = (DataContainer) loadXML(selectedFile.getPath());
        }
        return toLoad;
    }

    /**
     * Save the Fighter object in a XML file.
     * @param toSave The Fighter object to be saved.
     */
    public void saveFighter(Fighter toSave) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("saveParticipant"));
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(rb.getString("extFilter"), "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            saveXML(toSave, selectedFile.getPath());
        }
    }

    /**
     * Loading saved Fighter object using XML files.
     * @return The Fighter Object contained in the saved file.
     */
    public Fighter loadFighter() {
        Fighter toLoad = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("loadParticipant"));
        fileChooser.setInitialDirectory(new File(location));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter(rb.getString("extFilter"), "*.eazi.xml"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            System.out.println(selectedFile.toString());
            toLoad = (Fighter) loadXML(selectedFile.getPath());
        }
        return toLoad;
    }

    /**
     * Function to save any object into a XML file. FileChooser not included.
     * @param object The serializable object to save.
     * @param file The string representation of the file path, including file name.
     */
    public synchronized void saveXML(Object object, String file) {
        XMLEncoder encoder = null;
        try {
            encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
            encoder.writeObject(object);
        } catch(IOException e) {
            new InfoDialog(rb.getString("errorSave")).showAndWait();
        } finally {
            if (encoder != null) {
                encoder.flush();
                encoder.close();
            }
        } // end of try
    }

    /**
     * Function to load a XML file data into an object. FileChooser not included.
     * @param file The path to the file, including the file name.
     * @return Object containing the data from the XML file. Must be casted to the required object.
     */
    public synchronized Object loadXML(String file) {
        Object loaded = new Object();
        try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)))) {
            loaded = decoder.readObject();
        } catch (IOException e) {
            new InfoDialog(rb.getString("errorLoad")).showAndWait();
        }
        return loaded;
    }

    /**
     * Write settings properties into a file.
     * Default directory is defined in <code>ConfigContainer.java</code>.
     * @param properties The Properties object to write.
     * @param comment The comment to write.
     */
    public static synchronized void writeConfigFile(Properties properties, String comment) {
        Path file = Paths.get(ConfigContainer.getSettingsFilePath().toString());
        try(FileOutputStream output = new FileOutputStream(file.toFile())) {
            if (properties != null) {
                properties.store(output, comment);
            }
        }
        catch (IOException e) {
            new InfoDialog(rb.getString("errorConfigSave")).showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Read properties file from directory.
     * @return Properties class containing data. If no file present returns null.
     */
    public static synchronized Properties readConfigFile() {
        Properties prp = new Properties();
        Path file = Paths.get(ConfigContainer.getSettingsFilePath().toString());

        if (!file.toString().isEmpty() && !file.toFile().isDirectory()) {
            try (FileInputStream input = new FileInputStream(file.toFile())) {
                prp.load(input);
            } catch (IOException e) {
                new InfoDialog(rb.getString("errorConfigLoad")).showAndWait();
                prp = ConfigContainer.makeDefaultConfigFile();
            }
        }
        return prp;
    }

    /**
     * Changes the value of a specified key in the configurations file.
     * @param key Parameter key to change.
     * @param value Corresponding value to the key.
     * @param comment Include a comment, use empty string if not required.
     */
    public static void editConfig(String key, String value, String comment) {
        Properties prp = readConfigFile();
        prp.setProperty(key, value);
        writeConfigFile(prp, comment);
    }

}
