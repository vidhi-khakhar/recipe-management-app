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
        List<String> ingredients = Arrays.asList("Bread", "Cherry Tomatoes", "Mozarella", "Pesto Sauce");
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
        original.setServings(2);
        original.setIngredients(Arrays.asList("Tomato", "Cheese"));
        original.setInstructions(Arrays.asList("Step 1", "Step 2"));

        // Clone with new title
        Recipe clone = original.cloneRecipe("Cloned Recipe");
        assertEquals("Cloned Recipe", clone.getTitle());

        // Check ingredients and instructions are copied 
        assertEquals(original.getIngredients(), clone.getIngredients());
        assertEquals(original.getInstructions(), clone.getInstructions());
        assertEquals(original.getLanguage(), clone.getLanguage());
        assertEquals(original.getServings(), clone.getServings());
    }

    @Test
    void testModifyingCloneRecipe() {
        Recipe original = new Recipe("The Original Pesto Panini", "en");
        original.setServings(2);
        original.setIngredients(Arrays.asList("Tomato", "Cheese"));
        original.setInstructions(Arrays.asList("Step 1", "Step 2"));

        // Clone with new title
        Recipe clone = original.cloneRecipe("Cloned Recipe");
        assertEquals("Cloned Recipe", clone.getTitle());

        // Check if modifying clone doesn't affect original
        clone.getIngredients().add("Basil");
        clone.getInstructions().add("Step 3");
        clone.setServings(5);

        assertNotEquals(original.getIngredients(), clone.getIngredients());
        assertNotEquals(original.getInstructions(), clone.getInstructions());
        assertNotEquals(original.getServings(), clone.getServings());
    }



    @Test
    void testEqualsSame() {
        Recipe testRecipe = new Recipe("Pesto Panini");
        assertTrue(testRecipe.equals(testRecipe));
    }

    @Test
    void testEqualSameValues() {
        Recipe testRecipe = new Recipe("Pesto Panini");
        Recipe testRecipe2 = new Recipe("Pesto Panini");
        assertEquals(testRecipe, testRecipe2);
    }

    @Test
    void testEqualsDifferentValues(){
        Recipe testRecipe = new Recipe("Pesto Panini");
        Recipe testRecipe2 = new Recipe("Pesto Pasta");
        assertNotEquals(testRecipe, testRecipe2);
    }

    @Test
    void testEqualsDifferentLanguages(){
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        Recipe testRecipe2 = new Recipe("Pesto Panini", "ro");
        assertNotEquals(testRecipe, testRecipe2);
    }

    @Test
    void testHashCodeSame() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        assertEquals(testRecipe.hashCode(), testRecipe.hashCode());
    }

    @Test
    void testHashCodeSameValues() {
        Recipe testRecipe = new Recipe("Pesto Panini", "nl");
        Recipe testRecipe2 = new Recipe("Pesto Panini", "nl");
        assertEquals(testRecipe.hashCode(), testRecipe2.hashCode());
    }

    @Test
    void testHashCode_notEqualDifferentValues() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        Recipe testRecipe2 = new Recipe("Pesto Panini", "ro");
        assertNotEquals(testRecipe.hashCode(), testRecipe2.hashCode());
    }

    @Test
    void testToString() {
        Recipe testRecipe = new Recipe("Pesto Panini", "en");
        assertTrue(testRecipe.toString().contains("Pesto Panini"));
        assertTrue(testRecipe.toString().contains("en"));
    }
  
}



