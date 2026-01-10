package client.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import client.model.ShoppingList;

public class ConfigService {

    private final ObjectMapper mapper =
        new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final File configFile;

    private static ConfigService instance;
    private static Config config;
    private static String configPath = "client/config.json";

    /**
     * Creates a ConfigService that reads from and writes to the given file path.
     * @param path the file path for the config file
     * @throws IOException if the file cannot be accessed
     */
    public ConfigService(String path) {
        this.configFile = new File(path);
    }

    /**
     * Singleton access
     */
    public static ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService(configPath);
            try {
                config = instance.load();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load config", e);
            }
        }
        return instance;
    }

    public static Config getConfig() {
        return getInstance() != null ? config : null;
    }

    /**
     * Reloads the config by recalling the load() method.
     */
    public static void reloadConfig() {
        try {
            config = getInstance().load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload config", e);
        }
    }

    public static void setConfigPath(String path) {
        configPath = path;
    }

     /**
     * Loads the configuration from disk, or creates a default one if no file exists.
     * @return the loaded or newly created Config
     * @throws IOException if reading the file fails
     */
    public Config load() throws IOException {
        if (!configFile.exists()) {
            return createDefault();
        }

        String json = FileUtils.readFileToString(
            configFile,
            StandardCharsets.UTF_8
        );

        Config loaded = mapper.readValue(json, Config.class);

        // Safety: ensure shopping list always exists
        if (loaded.getShoppingList() == null) {
            loaded.setShoppingList(new ShoppingList());
        }

        return loaded;
    }

    /**
     * Saves the given Config object to disk as a JSON file.
     * @param config the configuration to persist
     * @throws IOException if writing the file fails
     */
    public void save(Config config) throws IOException {
        String json = mapper.writeValueAsString(config);

        FileUtils.writeStringToFile(
            configFile,
            json,
            StandardCharsets.UTF_8
        );
    }

    /**
     * This method initializes all required client-specific settings with default data
     * @return a fully initialized default Config object
     * @throws IOException if the configuration cannot be written to the file system
     */
    private Config createDefault() throws IOException {
        Config config = new Config();
        config.setServerUrl("http://localhost:8080");
        config.setFavoriteRecipeIds(new ArrayList<>());
        config.setLanguage(Locale.ENGLISH);
        config.setShoppingList(new ShoppingList()); // ✅ client-side list

        save(config);
        return config;
    }
}
