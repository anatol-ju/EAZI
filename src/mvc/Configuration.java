package mvc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class Configuration extends Properties {

    private static Configuration instance = null;

    private static final Path SETTINGS_FILE_PATH = Paths.get(
            System.getProperty("user.home"), ".eazi", "userSettings.properties");

    // thread safe initialization of object
    private static final class InstanceHolder {
        static final Configuration INSTANCE;
        static {
            INSTANCE = new Configuration();
            load();
        }
    }

    private Configuration() { }

    /**
     * Initialize a new configuration.
     */
    public static void init() {

        try {
            // make new directory to file
            if(SETTINGS_FILE_PATH.getParent() != null) {
                Files.createDirectories(SETTINGS_FILE_PATH.getParent());
            }
            // make file
            Files.createFile(SETTINGS_FILE_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InstanceHolder.INSTANCE.setProperty("savePath", SETTINGS_FILE_PATH.toString());
        InstanceHolder.INSTANCE.setProperty("actionCircleFieldCount", "12");
        InstanceHolder.INSTANCE.setProperty("autoSaveButton", "true");
        InstanceHolder.INSTANCE.setProperty("intervalBox", "false");
        InstanceHolder.INSTANCE.setProperty("autoSaveInterval", "2");   // don't forget to cast to int
        InstanceHolder.INSTANCE.setProperty("hideLogButton", "false");
        InstanceHolder.INSTANCE.setProperty("colorFighter", "50,205,50");   // red,green,blue
        InstanceHolder.INSTANCE.setProperty("colorAlly", "30,144,255");
        InstanceHolder.INSTANCE.setProperty("colorEnemy", "255,0,0");
        InstanceHolder.INSTANCE.setProperty("simpleActions", "6");
        InstanceHolder.INSTANCE.setProperty("simpleActionsSurcharge", "3");
        InstanceHolder.INSTANCE.setProperty("simpleReactions", "0");
        InstanceHolder.INSTANCE.setProperty("simpleReactionsSurcharge", "3");
        InstanceHolder.INSTANCE.setProperty("freeActions", "0");

        try (FileOutputStream output = new FileOutputStream(SETTINGS_FILE_PATH.toFile())) {
            InstanceHolder.INSTANCE.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new InfoDialog("A new configurations file had been created for you!\n" +
                SETTINGS_FILE_PATH).showAndWait();
    }

    /**
     * Load configuration file.
     */
    public static void load() {
        if (!SETTINGS_FILE_PATH.toString().isEmpty() && !SETTINGS_FILE_PATH.toFile().isDirectory()) {
            try (FileInputStream input = new FileInputStream(SETTINGS_FILE_PATH.toFile())) {
                InstanceHolder.INSTANCE.load(input);
            } catch (IOException e) {
                new InfoDialog(ResourceBundle.getBundle("locales.Serializer", Locale.getDefault()).getString("errorConfigLoad")).showAndWait();
                init();
            }
        }
    }

    /**
     * Save the current configuration to a file.
     * @param comment an optional string to attach to the file, use <c>null</c> if not needed.
     */
    public static void save(String comment) {
        try(FileOutputStream output = new FileOutputStream(SETTINGS_FILE_PATH.toFile())) {
            InstanceHolder.INSTANCE.store(output, comment);
        }
        catch (IOException e) {
            new InfoDialog(ResourceBundle.getBundle("locales.Serializer", Locale.getDefault()).getString("errorConfigSave")).showAndWait();
            e.printStackTrace();
        }
    }

    /**
     * Get the configuration properties.
     * @return the singleton instance of the configuration.
     */
    public static Configuration get() {
        return InstanceHolder.INSTANCE;
    }

    public static Path getSettingsFilePath() {
        return SETTINGS_FILE_PATH;
    }
}
