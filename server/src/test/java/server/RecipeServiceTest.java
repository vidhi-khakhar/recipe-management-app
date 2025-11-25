package server;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
     // Mock is used to create a fake version of the database
    @Mock
    private RecipeRepository recipeRepository;

    // Repository is injected here
    @InjectMocks
    private RecipeService recipeService;

    @Test
    void createRecipe() {
        Recipe recipe = new Recipe("Sarmale");
        recipeService.createRecipe(recipe);

        // Checks that save was called for repository exactly 1 time
        verify(recipeRepository, times(1)).save(recipe);
    }

    /**
     * By default, mocks do nothing with methods.
     * Behavior is defined with when().thenReturn()
     */
    @Test
    void getRecipeById() {
        Recipe recipe = new Recipe("Sarmale");
        Long id = recipe.getRecipeId();

        // Simulate behaviour of findById in database
        when(recipeRepository.findById(id))
                .thenReturn(Optional.of(recipe));
        
        assertEquals("Sarmale",
                recipeService.getRecipeById(id).getTitle());
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