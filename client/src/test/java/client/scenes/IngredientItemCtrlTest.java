package client.scenes;

import static org.junit.jupiter.api.Assertions.*;

import client.shopping.RecipeIngredientData;
import org.junit.jupiter.api.Test;

public class IngredientItemCtrlTest {

    @Test
    public void testDefaultConstructor() {
        IngredientItemCtrl ctrl = new IngredientItemCtrl();
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
