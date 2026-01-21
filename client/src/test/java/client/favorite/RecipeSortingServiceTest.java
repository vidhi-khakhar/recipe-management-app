package client.favorite;

import client.search.RecipeCollection;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecipeSortingServiceTest {
    private RecipeSortingService service;
    private List<Recipe> collection;

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
        recipeA = new Recipe("Apple Pie");
        recipeA.setRecipeId(1L);

        recipeB = new Recipe("Brownies");
        recipeB.setRecipeId(2L);

        recipeC = new Recipe("Carrot Cake");
        recipeC.setRecipeId(3L);

        RecipeCollection recipecollection = new RecipeCollection(List.of(recipeC, recipeA, recipeB));
        service = new RecipeSortingService(recipecollection);
        collection = new ArrayList<>(List.of(recipeC, recipeA, recipeB));
    }

    /**
     * Asserts whether a list that contains recipes can be sorted. Should return true.
     */
    @Test
    void isSortableTrue(){
        assertTrue(service.isSortable(collection));
    }

    /**
     * Asserts whether a list that contains recipes can be sorted. Should return false.
     */
    @Test
    void isSortableFalseWhenEmptyTest() {
        List<Recipe> empty = List.of();
        assertFalse(service.isSortable(empty));
    }

    /**
     * Asserts whether the created list of recipes gets sorted to
     * alphabetical order.
     */
    @Test
    void sortByTitleAscTest() {
        List<Recipe> result = service.sortByTitleAsc(collection);

        assertEquals("Apple Pie", result.get(0).getTitle());
        assertEquals("Brownies", result.get(1).getTitle());
        assertEquals("Carrot Cake", result.get(2).getTitle());
    }

    /**
     * Asserts whether the created list of recipes gets sorted to
     * alphabetical order.
     */
    @Test
    void sortByTitleDescTest() {
        List<Recipe> result = service.sortByTitleDesc(collection);

        assertEquals("Apple Pie", result.get(2).getTitle());
        assertEquals("Brownies", result.get(1).getTitle());
        assertEquals("Carrot Cake", result.get(0).getTitle());
    }

    /**
     * Tests whether a user can get the default ordering (by date OLD -> NEW) back.
     */
    @Test
    void sortByDateAscKeepsOriginalOrderTest() {
        List<Recipe> result = service.sortByDateAsc();

        // recipeC, recipeA, recipeB
        assertEquals("Carrot Cake", result.get(0).getTitle());
        assertEquals("Apple Pie", result.get(1).getTitle());
        assertEquals("Brownies", result.get(2).getTitle());
    }

    /**
     * Asserts whether the created list of recipes gets sorted by date (NEW -> OLD).
     * */
    @Test
    void sortByDateDescReversesOriginalOrderTest() {
        List<Recipe> result = service.sortByDateDesc();

        assertEquals("Brownies", result.get(0).getTitle());
        assertEquals("Apple Pie", result.get(1).getTitle());
        assertEquals("Carrot Cake", result.get(2).getTitle());
    }

    /**
     * Asserts whether the limit sorting function actually reduces the size of the list.
     */
    @Test
    void limitReducesListSizeTest() {
        List<Recipe> sorted = service.sortByTitleAsc(collection);
        List<Recipe> limited = service.limit(sorted, 2);

        assertEquals(2, limited.size());
        assertEquals("Apple Pie", limited.get(0).getTitle());
        assertEquals("Brownies", limited.get(1).getTitle());
    }

    /**
     * Asserts that if the limit parameter is larger than the list size, the parameter gets overwritten
     * by the size of the list.
     */
    @Test
    void limitLargerThanListSizeTest() {
        List<Recipe> sorted = service.sortByTitleAsc(collection);
        List<Recipe> limited = service.limit(sorted, 10);

        assertEquals(3, limited.size());
    }

    /**
     * Asserts whether the first recipe title is returned when at least one recipe
     * exists in the list.
     */
    @Test
    void getFirstReturnsFirstRecipeTest() {
        List<Recipe> sorted = service.sortByTitleAsc(collection);

        Optional<Recipe> first = service.getFirst(sorted);

        assertTrue(first.isPresent());
        assertEquals("Apple Pie", first.get().getTitle());
    }

    /**
     * Asserts whether the first recipe title is empty (not null or error) when a
     * given list has no recipes.
     * <br>
     * Testing behaviour of optional.
     */
    @Test
    void getFirstEmptyListTest() {
        Optional<Recipe> first = service.getFirst(List.of());

        assertTrue(first.isEmpty());
    }

    /**
     * Asserts whether the first recipe title is empty (not null or error) when a
     * given list is null.
     * <br>
     * Testing behaviour of optional.
     */
    @Test
    void getFirstNullListTest() {
        Optional<Recipe> first = service.getFirst(null);

        assertTrue(first.isEmpty());
    }


    /**
     * Asserts whether the preview returns a limited,
     * alphabetically sorted list of recipes.
     */
    @Test
    void getPreviewTest() {
        List<Recipe> preview = service.getPreview(collection, 2);

        assertEquals(2, preview.size());
        assertEquals("Apple Pie", preview.get(0).getTitle());
        assertEquals("Brownies", preview.get(1).getTitle());
    }
}