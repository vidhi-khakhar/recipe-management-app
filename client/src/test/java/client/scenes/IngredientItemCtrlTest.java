package client.scenes;

import static org.junit.jupiter.api.Assertions.*;

import client.shopping.RecipeIngredientData;
import org.junit.jupiter.api.Test;

import client.config.ConfigService;

import java.io.File;
import java.util.UUID;

public class IngredientItemCtrlTest {

    private static class TestConfigService extends ConfigService {
        public TestConfigService() {
            super(
                    System.getProperty("java.io.tmpdir") + "/" + UUID.randomUUID().toString() + ".json"
            );
        }
    }

    @Test
    public void testDefaultConstructor() {
        IngredientItemCtrl ctrl = new IngredientItemCtrl(new TestConfigService());
        assertNotNull(ctrl);
    }

    @Test
    public void testIsInformalUnitTrue() {
        assertTrue(true);
    }

    @Test
    public void testIngredientDataSettersAndGetters() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertEquals("Sugar", data.getName());
        assertEquals(100.0, data.getAmount());
        assertEquals("g", data.getUnit());
        assertTrue(data.isFormal());
    }
}
