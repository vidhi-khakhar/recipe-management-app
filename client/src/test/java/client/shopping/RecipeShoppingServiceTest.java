package client.shopping;

import static org.junit.jupiter.api.Assertions.*;

import client.config.ConfigService;
import client.model.ShoppingItem;
import client.utils.RecipeServerUtils;
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

public class RecipeShoppingServiceTest {

    @Mock
    private RecipeServerUtils mockRecipeServerUtils;

    @Mock
    private ConfigService mockConfigService;

    private RecipeShoppingService shoppingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        shoppingService = new RecipeShoppingService(
            mockRecipeServerUtils,
            mockConfigService
        );
    }

    @Test
    public void testGetRecipeIngredientsWithFormalIngredients() {
        Recipe recipe = new Recipe("Test Recipe");
        List<RecipeIngredient> ingredients = new ArrayList<>();

        Ingredient sugar = new Ingredient("Sugar", 0, 0, 100);
        RecipeIngredient ri = RecipeIngredient.formal(
            recipe,
            sugar,
            100.0,
            Unit.G
        );
        ingredients.add(ri);

        recipe.setIngredients(ingredients);

        List<RecipeIngredientData> result =
            shoppingService.getRecipeIngredients(recipe);

        assertEquals(1, result.size());
        assertEquals("Sugar", result.get(0).getName());
        assertTrue(result.get(0).isFormal());
    }

    @Test
    public void testGetRecipeIngredientsWithEmptyRecipe() {
        Recipe recipe = new Recipe("Empty Recipe");
        recipe.setIngredients(new ArrayList<>());

        List<RecipeIngredientData> result =
            shoppingService.getRecipeIngredients(recipe);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetRecipeIngredientsWithNullIngredients() {
        Recipe recipe = new Recipe("Null Ingredients Recipe");
        recipe.setIngredients(null);

        List<RecipeIngredientData> result =
            shoppingService.getRecipeIngredients(recipe);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testConvertToShoppingItemsWithScaling() {
        List<RecipeIngredientData> ingredients = new ArrayList<>();
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        data.setFormal(true);
        ingredients.add(data);

        List<ShoppingItem> result = shoppingService.convertToShoppingItems(
            ingredients,
            4,
            8,
            "Test Recipe"
        );

        assertEquals(1, result.size());
        assertEquals("Sugar", result.get(0).getName());
        assertEquals(200.0, result.get(0).getAmount(), 0.01);
        assertEquals("g", result.get(0).getUnit());
        assertEquals("Test Recipe", result.get(0).getSourceRecipeName());
    }

    @Test
    public void testConvertToShoppingItemsWithoutScaling() {
        List<RecipeIngredientData> ingredients = new ArrayList<>();
        RecipeIngredientData data = new RecipeIngredientData(
            "Salt",
            "to taste"
        );
        data.setFormal(false);
        ingredients.add(data);

        List<ShoppingItem> result = shoppingService.convertToShoppingItems(
            ingredients,
            4,
            4,
            "Test Recipe"
        );

        assertEquals(1, result.size());
        assertEquals("Salt", result.get(0).getName());
        assertEquals(0.0, result.get(0).getAmount());
        assertEquals("to taste", result.get(0).getUnit());
    }

    @Test
    public void testConvertToShoppingItemsAlreadyScaled() {
        List<RecipeIngredientData> ingredients = new ArrayList<>();
        RecipeIngredientData data = new RecipeIngredientData(
            "Flour",
            200.0,
            "g"
        );
        data.setFormal(true);
        ingredients.add(data);

        List<ShoppingItem> result = shoppingService.convertToShoppingItems(
            ingredients,
            4,
            8,
            "Test Recipe",
            true
        );

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getAmount(), 0.01);
    }

    @Test
    public void testScaleIngredients() {
        List<RecipeIngredientData> ingredients = new ArrayList<>();
        RecipeIngredientData data = new RecipeIngredientData(
            "Sugar",
            100.0,
            "g"
        );
        data.setFormal(true);
        ingredients.add(data);

        List<RecipeIngredientData> result = shoppingService.scaleIngredients(
            ingredients,
            4,
            8
        );

        assertEquals(1, result.size());
        assertEquals(200.0, result.get(0).getAmount(), 0.01);
        assertTrue(result.get(0).isFormal());
    }

    @Test
    public void testScaleIngredientsWithInformal() {
        List<RecipeIngredientData> ingredients = new ArrayList<>();
        RecipeIngredientData data = new RecipeIngredientData(
            "Salt",
            "to taste"
        );
        data.setFormal(false);
        ingredients.add(data);

        List<RecipeIngredientData> result = shoppingService.scaleIngredients(
            ingredients,
            4,
            8
        );

        assertEquals(1, result.size());
        assertFalse(result.get(0).isFormal());
        assertEquals("to taste", result.get(0).getInformal());
    }

    @Test
    public void testConstructor() {
        RecipeShoppingService service = new RecipeShoppingService(
            mockRecipeServerUtils,
            mockConfigService
        );
        assertNotNull(service);
    }
}
