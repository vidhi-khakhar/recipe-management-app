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

/**
 * The class with the business logic for Config.
 * <br><br>
 * To access this class make sure to follow the steps in your own class:<br>
 * 1. Create a field 'ConfigService configService';<br>
 * 2. Use an @Inject constructor and have
 *    'ConfigService configService' as a parameter;<br>
 * 3. Assign parameter to field: this.configService = configService.
 *<br><br>
 * Now all methods from this class (and by extend Config) can be used in your class.
 * Make sure to always use configService.save() after making changes to Config.
 */
@Singleton
public class ConfigService {

    // Makes the list readable
    private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private final File configFile;
    private Config config;

    /**
     * Creates a ConfigService that reads from and writes to the given file path.
     * @param configPath the location of the configuration file
     *                   this can be either default or passed through
     *                   in the program parameters
     */
    @Inject
    public ConfigService(@Named("configPath") String configPath) {
        this.configFile = new File(configPath);

        // Safety for when given config path does not exist, it automatically creates it
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
     * This way all files get access to the same state of the Config Class
     * @return the same state of the config
     */
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
     * Saves the current version of the Config class to disk.
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

        this.config = config;
        save();

        return config;
    }
}
