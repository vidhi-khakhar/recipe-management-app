package client.favorite;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import client.config.Config;
import client.config.ConfigService;
import commons.Recipe;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FavoriteServiceTest {

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private Config mockConfig;

    private FavoriteService favoriteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mockConfigService.getConfig()).thenReturn(mockConfig);
        when(mockConfig.getFavoriteRecipeIds()).thenReturn(new ArrayList<>());
        favoriteService = new FavoriteService(mockConfigService);
    }

    @Test
    public void testConstructor() {
        FavoriteService service = new FavoriteService(mockConfigService);
        assertNotNull(service);
    }

    @Test
    public void testIsFavoriteWithNullRecipe() {
        boolean result = favoriteService.isFavorite(null);

        assertFalse(result);
    }

    @Test
    public void testIsFavoriteWithNullRecipeId() {
        Recipe recipe = new Recipe("Test");
        boolean result = favoriteService.isFavorite(recipe);

        assertFalse(result);
    }

    @Test
    public void testIsFavoriteWithFavoriteId() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);
        favoriteService.addFavorite(recipe);

        boolean result = favoriteService.isFavorite(recipe);

        assertTrue(result);
    }

    @Test
    public void testAddFavorite() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);

        boolean result = favoriteService.addFavorite(recipe);

        assertTrue(result);
    }

    @Test
    public void testAddFavoriteNull() {
        boolean result = favoriteService.addFavorite(null);

        assertFalse(result);
    }

    @Test
    public void testRemoveFavorite() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);
        favoriteService.addFavorite(recipe);

        boolean result = favoriteService.removeFavorite(recipe);

        assertTrue(result);
    }

    @Test
    public void testToggleFavoriteAdd() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);

        boolean result = favoriteService.toggleFavorite(recipe);

        assertTrue(result);
        assertTrue(favoriteService.isFavorite(recipe));
    }

    @Test
    public void testToggleFavoriteRemove() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);
        favoriteService.addFavorite(recipe);

        boolean result = favoriteService.toggleFavorite(recipe);

        assertTrue(result);
        assertFalse(favoriteService.isFavorite(recipe));
    }

    @Test
    public void testGetFavoriteRecipes() {
        Recipe recipe1 = new Recipe("Test1");
        recipe1.setRecipeId(1L);
        Recipe recipe2 = new Recipe("Test2");
        recipe2.setRecipeId(2L);
        favoriteService.addFavorite(recipe1);

        List<Recipe> allRecipes = new ArrayList<>();
        allRecipes.add(recipe1);
        allRecipes.add(recipe2);

        List<Recipe> favorites = favoriteService.getFavoriteRecipes(allRecipes);

        assertEquals(1, favorites.size());
        assertEquals(1L, favorites.get(0).getRecipeId());
    }

    @Test
    public void testGetFavoriteRecipeIDs() {
        Recipe recipe = new Recipe("Test");
        recipe.setRecipeId(1L);
        favoriteService.addFavorite(recipe);

        var result = favoriteService.getFavoriteRecipeIDs();

        assertTrue(result.contains(1L));
    }
}
