package Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads private settings (database login, e-mail account) from
 * {@code config.properties} in the project / program folder.
 *
 * config.properties is NOT stored in Git. Copy config.example.properties
 * to config.properties and fill in your own values.
 */
public final class AppConfig {

    private static final Properties PROPS = new Properties();

    static {
        File file = new File("config.properties");
        if (file.isFile()) {
            try (InputStream in = new FileInputStream(file)) {
                PROPS.load(in);
            } catch (Exception e) {
                System.err.println("Could not read config.properties: " + e.getMessage());
            }
        } else {
            System.err.println("config.properties not found in " + new File(".").getAbsolutePath()
                    + " - using default settings. Copy config.example.properties to config.properties.");
        }
    }

    private AppConfig() {
    }

    public static String get(String key, String defaultValue) {
        String v = PROPS.getProperty(key);
        return v == null ? defaultValue : v.trim();
    }

    public static String get(String key) {
        return get(key, "");
    }
}
