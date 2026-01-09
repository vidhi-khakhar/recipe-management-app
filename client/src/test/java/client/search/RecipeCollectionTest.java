package client.search;

import commons.Recipe;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecipeCollectionTest {

    // --- helpers -------------------------------------------------------------

    private static Recipe recipeWithId(long id, String title) {
        Recipe r = new Recipe(title);
        setRecipeId(r, id);
        return r;
    }

    private static void setRecipeId(Recipe recipe, long id) {
        try {
            Field f = Recipe.class.getDeclaredField("recipeId");
            f.setAccessible(true);
            f.set(recipe, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set recipeId via reflection", e);
        }
    }

    // --- tests ---------------------------------------------------------------

    @Test
    void newCollection_isEmpty() {
        RecipeCollection c = new RecipeCollection();
        assertTrue(c.isEmpty());
    }

    @Test
    void constructorWithRecipes_addsAll() {
        Recipe a = recipeWithId(1, "Apple Pie");
        Recipe b = recipeWithId(2, "Banana Bread");
        RecipeCollection c = new RecipeCollection(List.of(a, b));

        assertEquals(2, c.size());
        assertEquals(List.of(a, b), c.getAll()); // preserves insertion order
    }

    @Test
    void add_nullRecipe_throwsNullPointerException() {
        RecipeCollection c = new RecipeCollection();
        assertThrows(NullPointerException.class, () -> c.add(null));
    }

    @Test
    void add_recipeWithoutId_throwsIllegalArgumentException() {
        RecipeCollection c = new RecipeCollection();
        Recipe r = new Recipe("No ID Yet"); // recipeId stays null

        assertThrows(IllegalArgumentException.class, () -> c.add(r));
        assertTrue(c.isEmpty());
    }

    @Test
    void add_and_getById_works() {
        RecipeCollection c = new RecipeCollection();
        Recipe r = recipeWithId(10, "Lasagna");

        c.add(r);

        Optional<Recipe> found = c.getById(10L);
        assertTrue(found.isPresent());
        assertSame(r, found.get());
    }

    @Test
    void getById_null_returnsEmpty() {
        RecipeCollection c = new RecipeCollection();
        assertEquals(Optional.empty(), c.getById(null));
    }

    @Test
    void remove_existingId_removesRecipe() {
        RecipeCollection c = new RecipeCollection();
        Recipe r = recipeWithId(5, "Soup");

        c.add(r);

        c.remove(5L);
        assertEquals(Optional.empty(), c.getById(5L));
    }


    @Test
    void clear_removesEverything() {
        RecipeCollection c = new RecipeCollection();
        c.add(recipeWithId(1, "A"));
        c.add(recipeWithId(2, "B"));

        c.clear();

        assertTrue(c.isEmpty());
    }

    @Test
    void add_withSameId_replacesRecipe() {
        RecipeCollection c = new RecipeCollection();
        Recipe oldR = recipeWithId(7, "Old Title");
        Recipe newR = recipeWithId(7, "New Title");

        c.add(oldR);
        c.add(newR);

        assertEquals(1, c.size());
    }




}


