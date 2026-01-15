package client.scenes;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import client.MyFXML;
import client.model.ShoppingItem;
import client.shopping.RecipeIngredientData;
import client.shopping.RecipeShoppingService;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeShoppingListCtrlTest {

    private RecipeShoppingListCtrl controller;
    private TestRecipeShoppingService testService;

    @BeforeEach
    void setUp() {
        testService = new TestRecipeShoppingService();
        MyFXML myFXML = new TestMyFXML();
        controller = new RecipeShoppingListCtrl(testService, myFXML);
    }

    @Test
    void constructor_initializesFields() {
        assertNotNull(controller);
    }

    @Test
    void constructor_initializesIngredientLists() {
        List<RecipeIngredientData> ingredients = controller.getIngredientsForTest();
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void constructor_initializesIngredientCtrlsList() {
        List<IngredientItemCtrl> ctrls = controller.getIngredientCtrlsForTest();
        assertNotNull(ctrls);
        assertTrue(ctrls.isEmpty());
    }

    @Test
    void setCurrentRecipe_setsRecipe() {
        Recipe recipe = createTestRecipe("Test Recipe", 4);

        controller.setCurrentRecipeForTest(recipe);

        assertEquals(recipe, controller.getCurrentRecipeForTest());
    }

    @Test
    void setOriginalServings_setsServings() {
        int servings = 6;

        controller.setOriginalServingsForTest(servings);

        assertEquals(servings, controller.getOriginalServingsForTest());
    }

    @Test
    void addIngredientToList_addsNewIngredient() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        List<RecipeIngredientData> ingredients = controller.getIngredientsForTest();

        controller.getIngredientsForTest().add(ingredient);

        assertEquals(1, ingredients.size());
        assertEquals("Flour", ingredients.get(0).getName());
    }

    @Test
    void removeIngredient_removesFromList() {
        RecipeIngredientData flour = new RecipeIngredientData("Flour", 200.0, "g");
        RecipeIngredientData sugar = new RecipeIngredientData("Sugar", 100.0, "g");
        controller.getIngredientsForTest().add(flour);
        controller.getIngredientsForTest().add(sugar);

        controller.getIngredientsForTest().remove(flour);

        assertEquals(1, controller.getIngredientsForTest().size());
        assertEquals("Sugar", controller.getIngredientsForTest().get(0).getName());
    }

    @Test
    void getIngredientData_returnsIngredientData() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(ingredient);

        List<RecipeIngredientData> result = controller.getIngredientsForTest();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void setRecipe_updatesCurrentRecipe() {
        Recipe recipe = new Recipe("Updated Recipe", "en");
        recipe.setServings(6);
        controller.setCurrentRecipeForTest(recipe);

        Recipe stored = controller.getCurrentRecipeForTest();
        assertNotNull(stored);
        assertEquals("Updated Recipe", stored.getTitle());
    }

    @Test
    void setServings_updatesOriginalServings() {
        controller.setOriginalServingsForTest(6);

        assertEquals(6, controller.getOriginalServingsForTest());
    }

    @Test
    void ingredientList_preservesOrder() {
        RecipeIngredientData flour = new RecipeIngredientData("Flour", 200.0, "g");
        RecipeIngredientData sugar = new RecipeIngredientData("Sugar", 100.0, "g");
        RecipeIngredientData salt = new RecipeIngredientData("Salt", 1.0, "tsp");

        controller.getIngredientsForTest().add(flour);
        controller.getIngredientsForTest().add(sugar);
        controller.getIngredientsForTest().add(salt);

        assertEquals(3, controller.getIngredientsForTest().size());
        assertEquals("Flour", controller.getIngredientsForTest().get(0).getName());
        assertEquals("Sugar", controller.getIngredientsForTest().get(1).getName());
        assertEquals("Salt", controller.getIngredientsForTest().get(2).getName());
    }

    @Test
    void ingredientData_copy_createsNewInstance() {
        RecipeIngredientData original = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(original);

        RecipeIngredientData copy = controller.getIngredientsForTest().get(0).copy();

        assertNotSame(original, copy);
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAmount(), copy.getAmount());
    }

    @Test
    void ingredientData_setName_updatesName() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(ingredient);

        ingredient.setName("Updated Flour");

        assertEquals("Updated Flour", ingredient.getName());
    }

    @Test
    void ingredientData_setAmount_updatesAmount() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(ingredient);

        ingredient.setAmount(300.0);

        assertEquals(300.0, ingredient.getAmount(), 0.01);
    }

    @Test
    void ingredientData_isFormal_returnsTrueForFormalIngredient() {
        RecipeIngredientData formalIngredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(formalIngredient);

        assertTrue(controller.getIngredientsForTest().get(0).isFormal());
    }

    @Test
    void ingredientData_isCustomAdded_initiallyFalse() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(ingredient);

        assertFalse(controller.getIngredientsForTest().get(0).isCustomAdded());
    }

    @Test
    void ingredientData_setCustomAdded_setsFlag() {
        RecipeIngredientData ingredient = new RecipeIngredientData("Flour", 200.0, "g");
        controller.getIngredientsForTest().add(ingredient);

        ingredient.setCustomAdded(true);

        assertTrue(ingredient.isCustomAdded());
    }

    private Recipe createTestRecipe(String title, int servings) {
        Recipe recipe = new Recipe(title, "en");
        recipe.setServings(servings);
        return recipe;
    }

    private static class TestRecipeShoppingService extends RecipeShoppingService {
        private List<RecipeIngredientData> ingredients;
        private List<ShoppingItem> convertedItems;
        private boolean getIngredientsCalled = false;
        private boolean addToShoppingListCalled = false;
        private List<RecipeIngredientData> scaledIngredients;
        private boolean scaleIngredientsCalled = false;

        public TestRecipeShoppingService() {
            super(null, null);
        }

        @Override
        public List<RecipeIngredientData> getRecipeIngredients(Recipe recipe) {
            getIngredientsCalled = true;
            return ingredients != null ? ingredients : new ArrayList<>();
        }

        @Override
        public List<ShoppingItem> convertToShoppingItems(
                List<RecipeIngredientData> ingredients, int originalServings,
                int targetServings, String recipeName, boolean alreadyScaled) {
            return convertedItems != null ? convertedItems : new ArrayList<>();
        }

        @Override
        public void addToShoppingList(List<ShoppingItem> shoppingItems) {
            addToShoppingListCalled = true;
        }

        @Override
        public List<RecipeIngredientData> scaleIngredients(
                List<RecipeIngredientData> ingredients, int originalServings, int targetServings) {
            scaleIngredientsCalled = true;
            return scaledIngredients != null ? scaledIngredients : new ArrayList<>();
        }

        void setIngredients(List<RecipeIngredientData> ingredients) {
            this.ingredients = ingredients;
        }

        void setConvertedItems(List<ShoppingItem> convertedItems) {
            this.convertedItems = convertedItems;
        }

        void setScaledIngredients(List<RecipeIngredientData> scaledIngredients) {
            this.scaledIngredients = scaledIngredients;
        }

        boolean wasScaleIngredientsCalled() {
            return scaleIngredientsCalled;
        }

        boolean wasGetIngredientsCalled() {
            return getIngredientsCalled;
        }

        boolean wasAddToShoppingListCalled() {
            return addToShoppingListCalled;
        }
    }

    private static class TestMyFXML extends MyFXML {
        public TestMyFXML() {
            super(null);
        }

        @Override
        public <T> javafx.util.Pair<T, javafx.scene.Parent> load(Class<T> c, String... parts) {
            return null;
        }
    }
}
