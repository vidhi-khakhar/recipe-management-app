package client.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import client.model.ShoppingList;

@Singleton
public class ConfigService {

    // Makes the list readable
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final File configFile;
    private Config config;

    /**
     * Creates a ConfigService that reads from and writes to the given file path.
     * @param configPath the location of the configuration file
     */
    @Inject
    public ConfigService(@Named("configPath") String configPath) {
        this.configFile = new File(configPath);
        try {
            this.config = load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }


    public Config getConfig() {
        return config;
    }

    /** Reloads the config from disk */
    public void reload() {
        try {
            this.config = load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload config", e);
        }
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
     */
    public void save() {
        try {
            String json = mapper.writeValueAsString(config);
            FileUtils.writeStringToFile(
                    configFile,
                    json,
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config", e);
        }
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
        config.setShoppingList(new ShoppingList());

        save();
        return config;
    }
}
