package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    /**
     * Test cases for the Ingredient class.
     */
    @Test
    void testConstructorWithValidValues() {
        Ingredient ingredient = new Ingredient(123L, "Sugar", 0.0, 0.0, 100.0);

        assertEquals(123, ingredient.getId());
        assertEquals("Sugar", ingredient.getName());
        assertEquals(0.0, ingredient.getProteinPer100g(), 0.0001);
        assertEquals(0.0, ingredient.getFatPer100g(), 0.0001);
        assertEquals(100.0, ingredient.getCarbsPer100g(), 0.0001);
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
        assertThrows(NullPointerException.class, () -> { new Ingredient(132L, null, 0.0, 0.0, 0.0);}
        );
        
    }

    @Test
    void testIdCannotBeNull() {
        assertThrows(NullPointerException.class, () -> { new Ingredient(132L, null, 0.0, 0.0, 0.0);}
        );

    }
}
