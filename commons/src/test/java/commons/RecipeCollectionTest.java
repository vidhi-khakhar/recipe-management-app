package commons;

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

    @Test
    void searchByTitle_nullOrBlank_returnsAll() {
        Recipe a = recipeWithId(1, "Banana Bread");
        Recipe b = recipeWithId(2, "Apple Pie");
        RecipeCollection c = new RecipeCollection(List.of(a, b));

        assertEquals(List.of(a, b), c.searchByTitle(null));
        assertEquals(List.of(a, b), c.searchByTitle(""));
        assertEquals(List.of(a, b), c.searchByTitle(" "));
    }

    @Test
    void searchByTitle_isCaseInsensitive_containsMatch_andSortedByTitle() {
        Recipe r1 = recipeWithId(1, "banana bread");
        Recipe r2 = recipeWithId(2, "Apple Pie");
        Recipe r3 = recipeWithId(3, "apricot tart");

        RecipeCollection c = new RecipeCollection(List.of(r1, r2, r3));

        List<Recipe> res = c.searchByTitle("A");
        assertEquals(List.of(r2, r3, r1), res);
    }

    @Test
    void filter_createsNewCollection() {
        Recipe r1 = recipeWithId(1, "Apple Pie");
        Recipe r2 = recipeWithId(2, "Banana Bread");
        Recipe r3 = recipeWithId(3, "Avocado Toast");

        RecipeCollection original = new RecipeCollection(List.of(r1, r2, r3));

        RecipeCollection onlyA = original.filter(r ->
                r.getTitle() != null && r.getTitle().toLowerCase().startsWith("a")
        );

        assertEquals(List.of(r1, r3), onlyA.getAll());
    }

    @Test
    void filter_nullPredicate_throwsNullPointerException() {
        RecipeCollection c = new RecipeCollection();
        assertThrows(NullPointerException.class, () -> c.filter(null));
    }


    @Test
    void equals_returnsFalseForNullAndDifferentClass() {
        RecipeCollection a = new RecipeCollection();
        assertNotEquals(a, null);
        assertNotEquals(a, "not a RecipeCollection");
    }

    @Test
    void HashCodeMatches_forSameContents() {
        RecipeCollection a = new RecipeCollection();
        RecipeCollection b = new RecipeCollection();

        a.add(recipeWithId(1, "Lasagna"));
        b.add(recipeWithId(1, "Lasagna"));

        assertEquals(a.hashCode(), b.hashCode());
    }


    @Test
    void equals_returnsFalse_whenContentsDiffer() {
        RecipeCollection a = new RecipeCollection();
        RecipeCollection b = new RecipeCollection();
        a.add(recipeWithId(1, "Lasagna"));
        b.add(recipeWithId(2, "Pizza"));
        assertNotEquals(a, b);
    }


    @Test
    void hashCode_changes_afterMutation() {
        RecipeCollection a = new RecipeCollection();
        a.add(recipeWithId(1, "Lasagna"));
        int before = a.hashCode();
        a.add(recipeWithId(2, "Pizza"));
        int after = a.hashCode();
        assertNotEquals(before, after);
    }
}


