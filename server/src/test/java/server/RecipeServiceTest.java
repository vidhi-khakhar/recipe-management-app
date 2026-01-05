package server;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import commons.Recipe;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {
     // Mock is used to create a fake version of the database
    @Mock
    private server.database.RecipeRepository recipeRepository;

    // Repository is injected here
    @InjectMocks
    private server.service.RecipeService recipeService;

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
        Recipe recipe1 = new Recipe("Sarmale");
        Recipe recipe2 = new Recipe("Ciorba Radauteana");

        when(recipeRepository.findAll())
                .thenReturn(Arrays.asList(recipe1, recipe2));

        assertEquals(2, recipeService.getAllRecipes().size());
    }

    @Test
    void updateRecipe() {
        Recipe recipeToUpdate  = new Recipe("Sarmale cu Vita-de-Vie");
        Long id  = recipeToUpdate.getRecipeId();
        when(recipeRepository.findById(id))
                .thenReturn(Optional.of(recipeToUpdate));

        Recipe updatedRecipe = new Recipe("Sarmale cu Varza");
        recipeService.updateRecipe(id, updatedRecipe);

        verify(recipeRepository, times(1)).save(updatedRecipe);
        assertEquals("Sarmale cu Varza", recipeService.getRecipeById(id).getTitle());
    }

    @Test
    void deleteRecipeById() {
        Recipe recipe = new Recipe("Sarmale");
        Long id = recipe.getRecipeId();

        when(recipeRepository.existsById(id))
                .thenReturn(true);
        recipeService.deleteRecipeById(id);

        verify(recipeRepository, times(1)).deleteById(id);
    }
}