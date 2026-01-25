package client.shopping;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RecipeIngredientDataTest {

    @Test
    public void testDefaultConstructor() {
        RecipeIngredientData data = new RecipeIngredientData();

        assertEquals("", data.getName());
        assertEquals(0.0, data.getAmount());
        assertEquals("", data.getUnit());
        assertFalse(data.isFormal());
        assertNull(data.getInformal());
        assertFalse(data.isCustomAdded());
    }

    @Test
    public void testInformalConstructor() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Salt",
            "to taste"
        );

        assertEquals("Salt", data.getName());
        assertEquals(0.0, data.getAmount());
        assertEquals("", data.getUnit());
        assertFalse(data.isFormal());
        assertEquals("to taste", data.getInformal());
        assertFalse(data.isCustomAdded());
    }

    @Test
    public void testFormalConstructor() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertEquals("Sugar", data.getName());
        assertEquals(100.0, data.getAmount());
        assertEquals("g", data.getUnit());
        assertTrue(data.isFormal());
        assertNull(data.getInformal());
        assertFalse(data.isCustomAdded());
    }

    @Test
    public void testCopy() {
        RecipeIngredientData original = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        original.setCustomAdded(true);

        RecipeIngredientData copy = original.copy();

        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAmount(), copy.getAmount());
        assertEquals(original.getUnit(), copy.getUnit());
        assertEquals(original.isFormal(), copy.isFormal());
        assertEquals(original.getInformal(), copy.getInformal());
        assertEquals(original.isCustomAdded(), copy.isCustomAdded());
        assertNotSame(original, copy);
    }

    @Test
    public void testSettersAndGetters() {
        RecipeIngredientData data = new RecipeIngredientData();

        data.setName("Flour");
        data.setAmount(200.0);
        data.setUnit("g");
        data.setFormal(true);
        data.setInformal(null);
        data.setCustomAdded(true);

        assertEquals("Flour", data.getName());
        assertEquals(200.0, data.getAmount());
        assertEquals("g", data.getUnit());
        assertTrue(data.isFormal());
        assertNull(data.getInformal());
        assertTrue(data.isCustomAdded());
    }

    @Test
    public void testGetDisplayTextFormal() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        String display = data.getDisplayText();

        assertTrue(display.contains("Sugar"));
        assertTrue(display.contains("100.0"));
        assertTrue(display.contains("g"));
    }

    @Test
    public void testGetDisplayTextInformal() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Salt",
            "to taste"
        );

        String display = data.getDisplayText();

        assertEquals("to taste", display);
    }

    @Test
    public void testToShoppingItem() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        data.setFormal(true);

        client.model.ShoppingItem item = data.toShoppingItem("Test Recipe");

        assertEquals("Sugar", item.getName());
        assertEquals(100.0, item.getAmount());
        assertEquals("g", item.getUnit());
        assertEquals("Test Recipe", item.getSourceRecipeName());
    }

    @Test
    public void testToStringFormal() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        String str = data.toString();

        assertTrue(str.contains("Sugar"));
        assertTrue(str.contains("100.0"));
        assertTrue(str.contains("g"));
    }

    @Test
    public void testToStringInformal() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Salt",
            "to taste"
        );

        String str = data.toString();

        assertTrue(str.contains("Salt"));
        assertTrue(str.contains("to taste"));
    }

    @Test
    public void testEqualsSameObject() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertEquals(data, data);
    }

    @Test
    public void testEqualsNull() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertNotEquals(null, data);
    }

    @Test
    public void testEqualsDifferentClass() {
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertNotEquals("String", data);
    }

    @Test
    public void testEqualsSameValues() {
        RecipeIngredientData data1 = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        RecipeIngredientData data2 = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertEquals(data1, data2);
    }

    @Test
    public void testEqualsDifferentValues() {
        RecipeIngredientData data1 = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        RecipeIngredientData data2 = new RecipeIngredientData(
            "Salt",
            50.0,
            "g"
        );

        assertNotEquals(data1, data2);
    }

    @Test
    public void testHashCode() {
        RecipeIngredientData data1 = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        RecipeIngredientData data2 = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );

        assertEquals(data1.hashCode(), data2.hashCode());
    }
}
