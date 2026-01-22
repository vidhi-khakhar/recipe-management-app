package client.scenes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.MyFXML;
import client.config.ConfigService;
import client.model.ShoppingItem;
import client.shopping.RecipeIngredientData;
import client.shopping.RecipeShoppingService;
import commons.Recipe;

class RecipeShoppingListCtrlTest {

    private RecipeShoppingListCtrl ctrl;
    private TestRecipeShoppingService service;

    @BeforeEach
    void setUp() {
        service = new TestRecipeShoppingService();
        MyFXML myFXML = new TestMyFXML();
        ConfigService configService = new TestConfigService();
        ctrl = new RecipeShoppingListCtrl(service, myFXML, configService);
    }

    @Test
    void init_notNull() {
        assertNotNull(ctrl);
    }

    @Test
    void ingredients_emptyOnInit() {
        List<RecipeIngredientData> ingredients = ctrl.getIngredientsForTest();
        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void ingredientCtrls_emptyOnInit() {
        List<IngredientItemCtrl> ctrls = ctrl.getIngredientCtrlsForTest();
        assertNotNull(ctrls);
        assertTrue(ctrls.isEmpty());
    }

    @Test
    void setRecipe_works() {
        Recipe recipe = createRecipe("Test", 4);
        ctrl.setCurrentRecipeForTest(recipe);
        assertEquals(recipe, ctrl.getCurrentRecipeForTest());
    }

    @Test
    void setServings_works() {
        ctrl.setOriginalServingsForTest(6);
        assertEquals(6, ctrl.getOriginalServingsForTest());
    }

    @Test
    void addIngredient_works() {
        RecipeIngredientData ing = new RecipeIngredientData("Flour", 200.0, "g");
        ctrl.getIngredientsForTest().add(ing);
        assertEquals(1, ctrl.getIngredientsForTest().size());
        assertEquals("Flour", ctrl.getIngredientsForTest().get(0).getName());
    }

    @Test
    void removeIngredient_works() {
        RecipeIngredientData f = new RecipeIngredientData("Flour", 200.0, "g");
        RecipeIngredientData s = new RecipeIngredientData("Sugar", 100.0, "g");
        ctrl.getIngredientsForTest().add(f);
        ctrl.getIngredientsForTest().add(s);
        ctrl.getIngredientsForTest().remove(f);
        assertEquals(1, ctrl.getIngredientsForTest().size());
        assertEquals("Sugar", ctrl.getIngredientsForTest().get(0).getName());
    }

    @Test
    void ingredientData_copyWorks() {
        RecipeIngredientData original = new RecipeIngredientData("Flour", 200.0, "g");
        ctrl.getIngredientsForTest().add(original);
        RecipeIngredientData copy = ctrl.getIngredientsForTest().get(0).copy();
        assertNotSame(original, copy);
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAmount(), copy.getAmount());
    }

    @Test
    void ingredient_setName() {
        RecipeIngredientData ing = new RecipeIngredientData("Flour", 200.0, "g");
        ing.setName("Sugar");
        assertEquals("Sugar", ing.getName());
    }

    @Test
    void ingredient_setAmount() {
        RecipeIngredientData ing = new RecipeIngredientData("Flour", 200.0, "g");
        ing.setAmount(300.0);
        assertEquals(300.0, ing.getAmount(), 0.01);
    }

    @Test
    void ingredient_isFormal() {
        RecipeIngredientData ing = new RecipeIngredientData("Flour", 200.0, "g");
        assertTrue(ing.isFormal());
    }

    @Test
    void ingredient_customFlag() {
        RecipeIngredientData ing = new RecipeIngredientData("Flour", 200.0, "g");
        assertFalse(ing.isCustomAdded());
        ing.setCustomAdded(true);
        assertTrue(ing.isCustomAdded());
    }

    @Test
    void ingredientList_orderPreserved() {
        RecipeIngredientData f = new RecipeIngredientData("Flour", 200.0, "g");
        RecipeIngredientData s = new RecipeIngredientData("Sugar", 100.0, "g");
        RecipeIngredientData salt = new RecipeIngredientData("Salt", 1.0, "tsp");
        ctrl.getIngredientsForTest().add(f);
        ctrl.getIngredientsForTest().add(s);
        ctrl.getIngredientsForTest().add(salt);
        assertEquals("Flour", ctrl.getIngredientsForTest().get(0).getName());
        assertEquals("Sugar", ctrl.getIngredientsForTest().get(1).getName());
        assertEquals("Salt", ctrl.getIngredientsForTest().get(2).getName());
    }

    private Recipe createRecipe(String title, int servings) {
        Recipe r = new Recipe(title, "en");
        r.setServings(servings);
        return r;
    }


    private static class TestRecipeShoppingService extends RecipeShoppingService {
        private List<RecipeIngredientData> ingredients;
        private List<ShoppingItem> converted;
        private List<RecipeIngredientData> scaled;

        public TestRecipeShoppingService() { super(null, null); }

        @Override
        public List<RecipeIngredientData> getRecipeIngredients(Recipe recipe) {
            return ingredients != null ? ingredients : new ArrayList<>();
        }

        @Override
        public List<ShoppingItem> convertToShoppingItems(List<RecipeIngredientData> ingredients,
                                                         int original, int target, String name, boolean scaled) {
            return converted != null ? converted : new ArrayList<>();
        }

        @Override
        public void addToShoppingList(List<ShoppingItem> items) { }

        @Override
        public List<RecipeIngredientData> scaleIngredients(List<RecipeIngredientData> ingredients,
                                                            int original, int target) {
            return scaled != null ? scaled : new ArrayList<>();
        }
    }

    private static class TestMyFXML extends MyFXML {
        public TestMyFXML() { super(null); }
        @Override
        public <T> javafx.util.Pair<T, javafx.scene.Parent> load(Class<T> c, String... parts) { return null; }
    }

    private static class TestConfigService extends ConfigService {
        public TestConfigService() { super("en"); }
        @Override
        public String getLanguage() { return "en"; }
    }
}
