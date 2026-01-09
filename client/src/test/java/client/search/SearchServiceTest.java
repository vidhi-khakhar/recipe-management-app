package client.search;

import client.favorite.FavoriteService;
import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchServiceTest {

    private RecipeCollection recipeCollection;

    private FavoriteService favoriteService;

    private SearchService searchService;

    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    public void setup() {

        recipeCollection = new RecipeCollection();
        favoriteService = new FavoriteService();

        r1 = new Recipe("Sandwich");
        r1.setRecipeId(1L);
        r1.setIngredients(Arrays.asList("Cheese", "Bread"));
        r1.setInstructions(Arrays.asList("Put bread", "Put cheese"));

        r2 = new Recipe("Boiled egg");
        r2.setRecipeId(2L);
        r2.setIngredients(Arrays.asList("egg"));
        r2.setInstructions(Arrays.asList("Boil water", "Put egg in water for 8 min", "Remove egg", "peel egg"));

        recipeCollection.add(r1);
        recipeCollection.add(r2);

        searchService = new SearchService(recipeCollection, favoriteService);
    }

    @Test
    public void emptySearchReturnsAllTest() {

        List<Recipe> results = searchService.search(" ", false);

        assertEquals(2,  results.size());
    }

    @Test
    public void searchIngredientsReturnsTest() {

        List<Recipe> results = searchService.search("cheese", false);

        assertEquals(1,  results.size());
        assertEquals("Sandwich", results.get(0).getTitle());
    }

    @Test
    public void searchMultipleTermsTest() {

        List<Recipe> results = searchService.search("boi gg", false);

        assertEquals(1,  results.size());
        assertEquals("Boiled egg", results.get(0).getTitle());
    }

    @Test
    public void favoriteFilteringTest() {

        favoriteService.addFavorite(r1);

        List<Recipe> results = searchService.search("Sandwich", true);

        assertEquals(1,  results.size());
        assertEquals("Sandwich", results.get(0).getTitle());
    }



}
