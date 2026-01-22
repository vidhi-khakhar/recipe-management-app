package client.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import client.model.ShoppingList;

/**
 * The class with the business logic for Config.
 */
@Singleton
public class ConfigService {

    // Makes the JSON readable
    private final ObjectMapper mapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private final File configFile;
    private Config config;

    /**
     * The constructor for the ConfigService class that links this class to the config file
     * @param configPath the path to the config file
     */
    @Inject
    public ConfigService(@Named("configPath") String configPath) {
        this.configFile = new File(configPath);

        // Ensure parent directories exist
        File parent = configFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try {
            this.config = load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    /**
     * Access the shared Config instance
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Reload config from disk
     */
    public void reload() {
        try {
            this.config = load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload config", e);
        }
    }

    /**
     * Load config or create default if missing
     */
    private Config load() throws IOException {
        if (!configFile.exists()) {
            return createDefault();
        }

        String json = FileUtils.readFileToString(
                configFile,
                StandardCharsets.UTF_8
        );

        Config loaded = mapper.readValue(json, Config.class);

        // Safety: shopping list must always exist
        if (loaded.getShoppingList() == null) {
            loaded.setShoppingList(new ShoppingList());
        }

        if (loaded.getFilteredLanguages() == null) {
            loaded.setFilteredLanguages(new ArrayList<>());

        // Safety: language must always exist
        if (loaded.getLanguage() == null || loaded.getLanguage().isBlank()) {
            loaded.setLanguage("en");

        }

        return loaded;
    }

    /**
     * Persist config to disk
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
     * Language helpers
     */
    public String getLanguage() {
        return config.getLanguage();
    }

    public void setLanguage(String languageCode) {
        config.setLanguage(languageCode);
        save();
    }

    /**
     * Create default config
     */
    private Config createDefault() throws IOException {
        Config config = new Config();
        config.setServerUrl("http://localhost:8080");
        config.setFavoriteRecipeIds(new ArrayList<>());
        config.setLanguage("en"); // default language
        config.setShoppingList(new ShoppingList());
        config.setFilteredLanguages(new ArrayList<>());

        this.config = config;
        save();

        return config;
    }
}
