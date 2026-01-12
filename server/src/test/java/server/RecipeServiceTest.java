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
        recipe.setRecipeId(1L);
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
        recipe.setRecipeId(1L);
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
        recipe1.setRecipeId(1L);
        Recipe recipe2 = new Recipe("Ciorba Radauteana");
        recipe2.setRecipeId(2L);

        when(recipeRepository.findAll())
                .thenReturn(Arrays.asList(recipe1, recipe2));

        assertEquals(2, recipeService.getAllRecipes().size());
    }

    @Test
    void updateRecipe() {
        Recipe recipeToUpdate  = new Recipe("Sarmale cu Vita-de-Vie");
        recipeToUpdate.setRecipeId(1L);
        Long id  = recipeToUpdate.getRecipeId();

        Recipe updatedRecipe = new Recipe("Sarmale cu Varza");

        when(recipeRepository.findById(id))
                .thenReturn(Optional.of(recipeToUpdate));


        recipeService.updateRecipe(id, updatedRecipe);

        verify(recipeRepository, times(1)).save(recipeToUpdate);
        assertEquals("Sarmale cu Varza", recipeToUpdate.getTitle());
    }

    @Test
    void deleteRecipeById() {
        Recipe recipe = new Recipe("Sarmale");
        recipe.setRecipeId(1L);
        Long id = recipe.getRecipeId();

        when(recipeRepository.existsById(id))
                .thenReturn(true);
        recipeService.deleteRecipeById(id);

        verify(recipeRepository, times(1)).deleteById(id);
    }
}