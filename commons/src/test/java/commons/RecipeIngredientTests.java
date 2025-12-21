package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientTest {

    private Recipe recipe;
    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        recipe = new Recipe() {
            @Override
            public Long getRecipeId() {
                return 1L;
            }
        };

        ingredient = new Ingredient() {
            @Override
            public Long getId() {
                return 50L;
            }
        };
    }

    @Test
    void createFormal_ShouldSetAmountAndUnit() {
        RecipeIngredient ri = RecipeIngredient.formal(recipe, ingredient, 100.0, Unit.G);

        assertEquals(100.0, ri.getAmount());
        assertEquals(Unit.G, ri.getUnit());
        assertNull(ri.getInformal());
    }

    @Test
    void createInformal_ShouldSetTextOnly() {
        RecipeIngredient ri = RecipeIngredient.informal(recipe, ingredient, "A pinch");

        assertEquals("A pinch", ri.getInformal());
        assertNull(ri.getAmount());
        assertNull(ri.getUnit());
    }

    @Test
    void setFormal_ShouldClearInformalData() {
        RecipeIngredient ri = RecipeIngredient.informal(recipe, ingredient, "A pinch");

        ri.setFormal(50.0, Unit.ML);

        assertEquals(50.0, ri.getAmount());
        assertNull(ri.getInformal());
    }

    @Test
    void setInformal_ShouldClearFormalData() {
        RecipeIngredient ri = RecipeIngredient.formal(recipe, ingredient, 100.0, Unit.G);

        ri.setInformal("To taste");

        assertEquals("To taste", ri.getInformal());
        assertNull(ri.getAmount());
        assertNull(ri.getUnit());
    }

    @Test
    void createFormal_ShouldThrowErrorIfInputsNull() {
        assertThrows(NullPointerException.class, () -> {
            RecipeIngredient.formal(null, ingredient, 10.0, Unit.G);
        });
    }
}