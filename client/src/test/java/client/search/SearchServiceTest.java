package client.search;

import client.config.ConfigService;
import client.favorite.FavoriteService;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class SearchServiceTest {


    private RecipeCollection recipeCollection;


    private ConfigService configService;


    private FavoriteService favoriteService;

    private SearchService searchService;

    private Recipe r1;
    private Recipe r2;

    @BeforeEach
    public void setup() {


        configService = new ConfigService("config-to-json2");

        recipeCollection = new RecipeCollection();
        favoriteService = new FavoriteService(configService);

        r1 = new Recipe("Sandwich");
        r1.setRecipeId(1L);

        Ingredient cheese = new Ingredient("Cheese", 0, 0, 0);
        cheese.setId(10L);
        Ingredient bread = new Ingredient("Bread", 0, 0, 0);
        bread.setId(11L);

        RecipeIngredient riC = RecipeIngredient.informal(r1, cheese, "1 Slice");
        RecipeIngredient riB = RecipeIngredient.informal(r1, bread, "2 Slice");

        r1.setIngredients(Arrays.asList(riC, riB));
        r1.setInstructions(Arrays.asList("Put bread", "Put cheese"));

        r2 = new Recipe("Boiled egg");
        r2.setRecipeId(2L);

        Ingredient egg = new Ingredient("egg", 0, 0, 0);
        egg.setId(12L);

        RecipeIngredient riE = RecipeIngredient.informal(r2, egg, "1 Large");
        r2.setIngredients(Arrays.asList(riE));
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
