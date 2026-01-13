package commons;

import commons.Recipe;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RecipeTest {
    @Test
    public void testTitledConstructor() {
        Recipe testRecipe = new Recipe("Pesto Panini");
        assertEquals("Pesto Panini", testRecipe.getTitle());
        assertEquals("en", testRecipe.getLanguage());
        assertEquals(1, testRecipe.getServings());
    }

    @Test
    public void testConstructor() {
        Recipe testRecipe = new Recipe("Pesto Panini", "RO");
        assertEquals("Pesto Panini", testRecipe.getTitle());
        assertEquals("ro", testRecipe.getLanguage());
        assertEquals(1, testRecipe.getServings());
    }


    @Test
    public void testSetTitle() {
        Recipe testRecipe = new Recipe("", "");
        testRecipe.setTitle("Pesto Pasta");
        assertEquals("Pesto Pasta", testRecipe.getTitle());
    }

    @Test
    public void testSetIngredients() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setRecipeId(1L);

        Ingredient bread = new Ingredient("Bread", 5, 5, 5);
        bread.setId(5L);
        Ingredient tomatoes = new Ingredient("Tomatoes", 5, 5, 5);
        tomatoes.setId(6L);

        RecipeIngredient ri1 = RecipeIngredient.informal(testRecipe, bread, "2 slices");
        RecipeIngredient ri2 = RecipeIngredient.formal(testRecipe, tomatoes, 10.0, Unit.G);

        List<RecipeIngredient> ingredients = Arrays.asList(ri1, ri2);
        testRecipe.setIngredients(ingredients);

        assertEquals(ingredients, testRecipe.getIngredients());
    }

    @Test
    public void testSetInstructions() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        List<String> instructions = Arrays.asList("First put pesto sauce on bread", "Then add tomatoes and mozarella", "Finally, grill it all");
        testRecipe.setInstructions(instructions);
        assertEquals(instructions, testRecipe.getInstructions());
    }

    @Test
    public void testSetLanguage() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setLanguage("RO");
        assertEquals("ro", testRecipe.getLanguage());
    }

    @Test
    public void testSetServings() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setServings(4);
        assertEquals(4, testRecipe.getServings());
    }

    @Test
    void testSuccessfulCloneRecipe() {
        Recipe original = new Recipe("The Original Pesto Panini", "en");
        original.setRecipeId(1L);
        original.setServings(2);

        Ingredient tomatoes = new Ingredient("Tomatoes", 5, 5, 5);
        tomatoes.setId(6L);
        RecipeIngredient ri = RecipeIngredient.formal(original, tomatoes, 10.0, Unit.G);
        List<RecipeIngredient> ingredients = Arrays.asList(ri);
        original.setIngredients(ingredients);

        original.setInstructions(Arrays.asList("Step 1", "Step 2"));

        // Clone with new title
        Recipe clone = original.cloneRecipe("Cloned Recipe");
        assertEquals("Cloned Recipe", clone.getTitle());

        // Check ingredients and instructions are copied 
        assertEquals(original.getIngredients().size(), clone.getIngredients().size());
        assertEquals(original.getInstructions(), clone.getInstructions());
        assertEquals(original.getLanguage(), clone.getLanguage());
        assertEquals(original.getServings(), clone.getServings());
    }

    @Test
    void testModifyingCloneRecipe() {
        Recipe original = new Recipe("The Original Pesto Panini", "en");
        original.setRecipeId(1L);
        original.setServings(2);

        Ingredient tomatoes = new Ingredient("Tomatoes", 5, 5, 5);
        tomatoes.setId(6L);

        RecipeIngredient ri = RecipeIngredient.formal(original, tomatoes, 10.0, Unit.G);

        List<RecipeIngredient> ingredients = Arrays.asList(ri);

        original.setInstructions(Arrays.asList("Step 1", "Step 2"));

        // Clone with new title
        Recipe clone = original.cloneRecipe("Cloned Recipe");
        assertEquals("Cloned Recipe", clone.getTitle());

        Ingredient basil = new Ingredient("basil", 5, 5, 5);
        basil.setId(7L);
        RecipeIngredient newRi = RecipeIngredient.informal(clone, basil, "5 leaves");
        // Check if modifying clone doesn't affect original
        clone.getIngredients().add(newRi);
        clone.getInstructions().add("Step 3");
        clone.setServings(5);

        assertNotEquals(original.getIngredients().size(), clone.getIngredients().size());
        assertNotEquals(original.getInstructions(), clone.getInstructions());
        assertNotEquals(original.getServings(), clone.getServings());
    }



    @Test
    void testEqualsSame() {
        Recipe testRecipe = new Recipe("Pesto Panini");
        testRecipe.setRecipeId(1L);
        assertTrue(testRecipe.equals(testRecipe));
    }

    @Test
    void testEqualSameValues() {
        Recipe testRecipe = new Recipe("Pesto Panini");
        testRecipe.setRecipeId(1L);
        Recipe testRecipe2 = new Recipe("Pesto Panini");
        testRecipe2.setRecipeId(1L);
        assertEquals(testRecipe, testRecipe2);
    }

    @Test
    void testEqualsDifferentValues(){
        Recipe testRecipe = new Recipe("Pesto Panini");
        testRecipe.setRecipeId(1L);
        Recipe testRecipe2 = new Recipe("Pesto Pasta");
        testRecipe2.setRecipeId(2L);
        assertNotEquals(testRecipe, testRecipe2);
    }

    @Test
    void testEqualsDifferentLanguages(){
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setRecipeId(1L);
        Recipe testRecipe2 = new Recipe("Pesto Panini", "ro");
        testRecipe2.setRecipeId(2L);
        assertNotEquals(testRecipe, testRecipe2);
    }

    @Test
    void testHashCodeSame() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setRecipeId(1L);
        assertEquals(testRecipe.hashCode(), testRecipe.hashCode());
    }

    @Test
    void testHashCodeSameValues() {
        Recipe testRecipe = new Recipe("Pesto Panini", "nl");
        testRecipe.setRecipeId(1L);
        Recipe testRecipe2 = new Recipe("Pesto Panini", "nl");
        testRecipe2.setRecipeId(1L);
        assertEquals(testRecipe.hashCode(), testRecipe2.hashCode());
    }

    @Test
    void testHashCode_notEqualDifferentValues() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        testRecipe.setRecipeId(1L);
        Recipe testRecipe2 = new Recipe("Pesto Panini", "ro");
        testRecipe2.setRecipeId(2L);
        assertNotEquals(testRecipe.hashCode(), testRecipe2.hashCode());
    }

    @Test
    void testToString() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        assertTrue(testRecipe.toString().contains("Pesto Panini"));
        assertTrue(testRecipe.toString().contains("en"));
    }
  
}



