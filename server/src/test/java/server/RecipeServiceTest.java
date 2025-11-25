package server;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecipeServiceTest {
    private RecipeRepository recipeRepository;
    private RecipeService recipeService;

    /**
     * Mock is used to create a fake version of the
     * database, so that the actual database is not messed with.
     */
    @BeforeEach
    void setUp() {
        recipeRepository = mock(RecipeRepository.class);
        recipeService = new RecipeService(recipeRepository);
    }
    @Test
    void createRecipe() {
        Recipe recipe = new Recipe("Sarmale");
        recipeService.createRecipe(recipe);

        // Checks that save was called for repository exactly 1 time
        verify(recipeRepository, times(1)).save(recipe);
    }

    @Test
    void getRecipeById() {
    }

    @Test
    void getAllRecipes() {
    }

    @Test
    void updateRecipe() {
    }

    @Test
    void deleteRecipeById() {
    }
}