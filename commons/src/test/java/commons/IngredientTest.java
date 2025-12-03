package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    /**
     * Test cases for the Ingredient class.
     */
    @Test
    void testConstructorWithValidValues() {
        Ingredient ingredient = new Ingredient("Sugar", 0.0, 0.0, 100.0, "en");

        assertEquals("Sugar", ingredient.getName());
        assertEquals(0.0, ingredient.getProteinPer100g(), 0.0001);
        assertEquals(0.0, ingredient.getFatPer100g(), 0.0001);
        assertEquals(100.0, ingredient.getCarbsPer100g(), 0.0001);
        assertEquals("en", ingredient.getLanguage());
    }

    /**
     * Test constructor with null or invalid language defaults to "en".
     */
    @Test
    void testConstructorWithNullLanguageDefaultsToEn() {
        Ingredient ingredient = new Ingredient("Milk", 3.4, 1.0, 5.0,null);
        assertEquals("en", ingredient.getLanguage());
    }

    /**
     * Test constructor with invalid language defaults to "en".
     */
    @Test
    void testConstructorWithInvalidLanguageDefaultsToEn() {
        Ingredient ingredient = new Ingredient("Milk", 3.4, 1.0, 5.0,"english");
        assertEquals("en", ingredient.getLanguage());
    }

    /**
     * Test that language is trimmed and lowercased.
     */
    @Test
    void testLanguageTrimsAndLowercases() {
        Ingredient ingredient = new Ingredient("Butter", 0.5, 81.0, 0.1,"FR");
        assertEquals("fr", ingredient.getLanguage());
    }

    /**
     * Test calculatingKcal method.
     */
    @Test
    void testCalculatingKcal() {
        double kcal = Ingredient.calculatingKcal(10.0, 5.0, 20.0);
        // kcal = 4*(10+20)+9*5 = 120+45 = 165
        assertEquals(165.0, kcal, 0.0001);
    }

    /**
     * Test that name cannot be null.
     */
    @Test
    void testNameCannotBeNull() {
        Exception exception = assertThrows(NullPointerException.class, () ->
            new Ingredient(null, 0.0, 0.0, 0.0,"en")
        );

        assertEquals("Name of an ingredient cannot be null", exception.getMessage());
    }
}
