package client.favorite;

import client.config.ConfigService;
import commons.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito.*;
import org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class FavoriteRecipeServiceTest {

    private ConfigService configService;

    private FavoriteService favoriteService;

    @BeforeEach
    public void setUp() {
        configService = new ConfigService("test_config.json");

        favoriteService = new FavoriteService(configService);
    }

    @AfterEach
    public void tearDown() {
        File file = new File("test_config.json");

        if (file.exists()) {
            file.delete();
        }
    }


    @Test
    void testIsFavorite() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);
        favoriteService.addFavorite(recipe);

        assertTrue(favoriteService.isFavorite(recipe));

        Recipe recipe2 = new Recipe("test2");
        recipe2.setRecipeId(200L);
        assertFalse(favoriteService.isFavorite(recipe2));
    }

    @Test
    void testAddFavorite() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);

        boolean result = favoriteService.addFavorite(recipe);
        assertTrue(result);

    }

    @Test
    void addDuplicateFails() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);
        favoriteService.addFavorite(recipe);

        boolean result = favoriteService.addFavorite(recipe);

        assertFalse(result);
    }

    @Test
    void deleteFavoriteTest() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);
        favoriteService.addFavorite(recipe);

        boolean result = favoriteService.removeFavorite(recipe);

        assertTrue(result);
        assertFalse(favoriteService.getFavoriteRecipeIDs().contains(100L));
    }

    @Test
    void toggleFavoriteTest() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);

        boolean toggled = favoriteService.toggleFavorite(recipe);
        assertTrue(toggled);
        assertTrue(favoriteService.isFavorite(recipe));

        boolean untoggled = favoriteService.toggleFavorite(recipe);
        assertTrue(untoggled);
        assertFalse(favoriteService.isFavorite(recipe));
    }

    @Test
    void getFavouriteRecipesTest() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);
        Recipe recipe2 = new Recipe("test2");
        recipe2.setRecipeId(200L);
        Recipe recipe3 = new Recipe("test3");
        recipe3.setRecipeId(300L);

        favoriteService.addFavorite(recipe);
        favoriteService.addFavorite(recipe2);

        List<Recipe> all = Arrays.asList(recipe, recipe2, recipe3);
        List<Recipe> result = favoriteService.getFavoriteRecipes(all);

        assertTrue(result.contains(recipe));
        assertTrue(result.contains(recipe2));
        assertFalse(result.contains(recipe3));

    }

    @Test
    void removeDeletedFavoriteTest() {
        Recipe recipe = new Recipe("test");
        recipe.setRecipeId(100L);
        Recipe recipe2 = new Recipe("test2");
        recipe2.setRecipeId(200L);
        Recipe recipe3 = new Recipe("test3");
        recipe3.setRecipeId(300L);

        favoriteService.addFavorite(recipe);
        favoriteService.addFavorite(recipe2);
        favoriteService.addFavorite(recipe3);

        //Server only has recipes 1 and 2 (3 was deleted by someone else)
        List<Recipe> serverRecipes = Arrays.asList(recipe, recipe2);

        List<Long> removedIds = favoriteService.removeDeletedFavorites(serverRecipes);

        assertEquals(1, removedIds.size());
        assertTrue(removedIds.contains(300L));
    }



}
