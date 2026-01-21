package client.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockitoAnnotations;

public class ConfigServiceTest {

    @TempDir
    Path tempDir;

    private ConfigService configService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testConstructorCreatesDefaultConfig() throws IOException {
        File configFile = tempDir.resolve("config.json").toFile();
        configService = new ConfigService(configFile.getAbsolutePath());

        Config config = configService.getConfig();

        assertNotNull(config);
        assertEquals("http://localhost:8080", config.getServerUrl());
        assertNotNull(config.getFavoriteRecipeIds());
        assertNotNull(config.getShoppingList());
    }

    @Test
    public void testGetConfig() throws IOException {
        File configFile = tempDir.resolve("config.json").toFile();
        configService = new ConfigService(configFile.getAbsolutePath());

        Config config = configService.getConfig();

        assertNotNull(config);
    }

    @Test
    public void testReload() throws IOException {
        File configFile = tempDir.resolve("config.json").toFile();
        configService = new ConfigService(configFile.getAbsolutePath());

        configService.reload();

        assertNotNull(configService.getConfig());
    }

    @Test
    public void testSave() throws IOException {
        File configFile = tempDir.resolve("config.json").toFile();
        configService = new ConfigService(configFile.getAbsolutePath());

        configService.save();

        assertTrue(configFile.exists());
    }
}
