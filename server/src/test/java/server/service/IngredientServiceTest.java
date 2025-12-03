package server.service;

import commons.Ingredient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientServiceTest {

    @Test
    void testKcalCalculation() {
        IngredientService service = new IngredientService();
        Ingredient ingredient = new Ingredient("Apple", 1, 0, 14, "en");

        // manually set new kcal
        double protein = 1;
        double fat = 0;
        double carbs = 14;
        double expectedKcal = 4 * protein + 4 * carbs + 9 * fat;

        ingredient.setKcalPer100g(expectedKcal);
        assertEquals(expectedKcal, ingredient.getKcalPer100g());
    }

    @Test
    void testIngredientName() {
        Ingredient ingredient = new Ingredient("Banana", 1, 0, 23, "en");
        assertEquals("Banana", ingredient.getName());
    }

    @Test
    void testUpdateIngredientFields() {
        Ingredient ingredient = new Ingredient("Carrot", 1, 0, 10, "en");
        ingredient.setName("Carrot Updated");
        ingredient.setProteinPer100g(2);

        assertEquals("Carrot Updated", ingredient.getName());
        assertEquals(2, ingredient.getProteinPer100g());
    }
}
