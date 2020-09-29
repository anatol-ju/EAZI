package mvc;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigContainer {

    private static final String SETTINGS_FILE_PATH =
            System.getProperty("user.home") + "//eazi//userConfig.properties";

    /**
     * Creates a new settings file in users home directory in case there is none.
     * Modify this to change any of the property names.
     */
    public static Properties makeDefaultConfigFile() {
        Properties prp = new Properties();

        try (FileOutputStream output = new FileOutputStream(SETTINGS_FILE_PATH)) {
            prp.setProperty("savePath", SETTINGS_FILE_PATH);
            prp.setProperty("autoSaveButton", "true");
            prp.setProperty("intervalBox", "false");
            prp.setProperty("autoSaveInterval", "2");   // don't forget to cast to int
            prp.setProperty("hideLogButton", "false");
            prp.setProperty("colorFighter", "#32CD32");
            prp.setProperty("colorAlly", "#0000CD");
            prp.setProperty("colorEnemy", "#FF0000");
            prp.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prp;
    }

    public static String getSettingsFilePath() {
        return SETTINGS_FILE_PATH;
    }
}
