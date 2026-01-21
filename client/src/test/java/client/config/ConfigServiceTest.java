package client.config;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import client.model.ShoppingList;

class ConfigServiceTest {

    private ConfigService configService;
    private File configFile;

    /**
     * Creates a temporary config file and
     * initializes a ConfigService before each test.
     */
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        configFile = tempDir.resolve("config.json").toFile();
        configService = new ConfigService(configFile.getAbsolutePath());
    }

    /**
     * Asserts that a default config is created
     * when no config file exists.
     */
    @Test
    void createsDefaultConfigWhenFileDoesNotExist() {
        Config config = configService.getConfig();

        assertNotNull(config);
        assertEquals("http://localhost:8080", config.getServerUrl());
        assertEquals("en", config.getLanguage()); // Changed to String
        assertNotNull(config.getShoppingList());
        assertNotNull(config.getFavoriteRecipeIds());
    }

    /**
     * Asserts that saving the config actually
     * writes data to disk.
     */
    @Test
    void saveWritesConfigToFile() {
        Config config = configService.getConfig();
        config.setLanguage("de"); // Changed to String

        configService.save();

        assertTrue(configFile.exists());
        assertTrue(configFile.length() > 0);
    }

    /**
     * Asserts that reloading the config restores
     * the saved values from disk.
     */
    @Test
    void reloadLoadsSavedConfig() {
        Config config = configService.getConfig();
        config.setLanguage("fr"); // Changed to String
        configService.save();

        configService.reload();
        Config reloaded = configService.getConfig();

        assertEquals("fr", reloaded.getLanguage());
    }

    /**
     * Asserts that a missing shopping list
     * is automatically initialized when loading.
     */
    @Test
    void shoppingListIsNeverNullAfterLoad() {
        Config config = configService.getConfig();
        config.setShoppingList(null);
        configService.save();

        configService.reload();
        Config reloaded = configService.getConfig();

        assertNotNull(reloaded.getShoppingList());
        assertTrue(reloaded.getShoppingList() instanceof ShoppingList);
    }
}
