package mvc;

import java.util.*;

public class LogMessageFactory {

    private Map<String, String[]> data;

    public LogMessageFactory() {
        data = new HashMap<>();
        initialize();
    }

    private void initialize() {
        ResourceBundle rb = ResourceBundle.getBundle("locales.RandomMessages", Locale.getDefault());
        Enumeration<String> keys = rb.getKeys();
        while (keys.hasMoreElements()) {
            String str =  keys.nextElement();
            data.put(str, (String[])rb.getObject(str));
        }
    }

    public String getRandomMessage(String identifier) {
        List<String> list = Arrays.asList(data.get(identifier));
        Collections.shuffle(list);

        return list.get(0);
    }

}
