package client.favorite;

import client.search.RecipeCollection;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeSortingServiceTest {
    private RecipeSortingService service;
    private RecipeCollection collection;

    private Recipe recipeA;
    private Recipe recipeB;
    private Recipe recipeC;

    /**
     * Sets up a RecipeSortingService and
     * a RecipeCollection with unsorted recipes
     * before each test.
     */
    @BeforeEach
    void setUp() {
        service = new RecipeSortingService();

        recipeA = new Recipe("Apple Pie");
        recipeA.setRecipeId(1L);

        recipeB = new Recipe("Brownies");
        recipeB.setRecipeId(2L);

        recipeC = new Recipe("Carrot Cake");
        recipeC.setRecipeId(3L);

        collection = new RecipeCollection(
                List.of(recipeC, recipeA, recipeB)
        );
    }

    /**
     * Asserts whether the created list of recipes gets sorted to
     * alphabetical order.
     */
    @Test
    void sortByTitle() {
        List<Recipe> result = service.sortByTitle(collection);

        assertEquals("Apple Pie", result.get(0).getTitle());
        assertEquals("Brownies", result.get(1).getTitle());
        assertEquals("Carrot Cake", result.get(2).getTitle());
    }

    /**
     * Asserts whether the preview returns a limited,
     * alphabetically sorted list of recipes.
     */
    @Test
    void getPreview() {
        List<Recipe> preview = service.getPreview(collection, 2);

        assertEquals(2, preview.size());
        assertEquals("Apple Pie", preview.get(0).getTitle());
        assertEquals("Brownies", preview.get(1).getTitle());
    }
}