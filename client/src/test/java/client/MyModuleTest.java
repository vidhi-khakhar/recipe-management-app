package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MyModuleTest {

    @Test
    public void testConstructorWithConfigPath() {
        String configPath = "test-config.json";
        MyModule module = new MyModule(configPath);

        assertNotNull(module);
    }

    @Test
    public void testModuleImplementsModule() {
        String configPath = "test-config.json";
        MyModule module = new MyModule(configPath);

        assertTrue(module instanceof com.google.inject.Module);
    }
}
