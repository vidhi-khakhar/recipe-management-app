package client.scenes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import client.config.ConfigService;
import client.utils.IngredientServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AddRecipeIngredientCtrlTest {

    @Mock
    private IngredientServerUtils mockIngredientServerUtils;

    @Mock
    private ConfigService mockConfigService;

    private AddRecipeIngredientCtrl addRecipeIngredientCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockConfigService.getLanguage()).thenReturn("en");
        addRecipeIngredientCtrl = new AddRecipeIngredientCtrl(
            mockIngredientServerUtils,
            mockConfigService
        );
    }

    @Test
    public void testConstructor() {
        AddRecipeIngredientCtrl ctrl = new AddRecipeIngredientCtrl(
            mockIngredientServerUtils,
            mockConfigService
        );
        assertNotNull(ctrl);
    }

    @Test
    public void testDuplicateIngredientReturnsTrue() {
        Recipe recipe = new Recipe("Test Recipe");
        Ingredient sugar = new Ingredient("Sugar", 0, 0, 100);
        RecipeIngredient ri = RecipeIngredient.formal(
            recipe,
            sugar,
            100.0,
            Unit.G
        );
        recipe.setIngredients(new ArrayList<>());
        recipe.getIngredients().add(ri);

        boolean result = addRecipeIngredientCtrl.duplicateIngredient(
            recipe,
            sugar
        );

        assertTrue(result);
    }

    @Test
    public void testDuplicateIngredientReturnsFalse() {
        Recipe recipe = new Recipe("Test Recipe");
        Ingredient sugar = new Ingredient("Sugar", 0, 0, 100);
        Ingredient salt = new Ingredient("Salt", 0, 0, 100);
        RecipeIngredient ri = RecipeIngredient.formal(
            recipe,
            sugar,
            100.0,
            Unit.G
        );
        recipe.setIngredients(new ArrayList<>());
        recipe.getIngredients().add(ri);

        boolean result = addRecipeIngredientCtrl.duplicateIngredient(
            recipe,
            salt
        );

        assertFalse(result);
    }

    @Test
    public void testDuplicateIngredientWithNullRecipe() {
        boolean result = addRecipeIngredientCtrl.duplicateIngredient(
            null,
            new Ingredient("Sugar", 0, 0, 100)
        );

        assertFalse(result);
    }

    @Test
    public void testValidateFormalAmountValid() {
        boolean result = addRecipeIngredientCtrl.validateFormalAmount("100.5");

        assertTrue(result);
    }

    @Test
    public void testValidateFormalAmountZero() {
        boolean result = addRecipeIngredientCtrl.validateFormalAmount("0");

        assertTrue(result);
    }

    @Test
    public void testValidateFormalAmountNegative() {
        boolean result = addRecipeIngredientCtrl.validateFormalAmount("-5");

        assertFalse(result);
    }

    @Test
    public void testValidateFormalAmountInvalidText() {
        boolean result = addRecipeIngredientCtrl.validateFormalAmount("abc");

        assertFalse(result);
    }

    @Test
    public void testSetRecipe() {
        Recipe recipe = new Recipe("Test");
        addRecipeIngredientCtrl.setRecipe(recipe);

        assertNotNull(addRecipeIngredientCtrl);
    }
}
