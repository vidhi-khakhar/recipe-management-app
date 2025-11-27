package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.Recipe;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.RecipeRepository;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeRepository repo;

    @InjectMocks
    private RecipeController recipeController;

    @Test
    void createsRecipeAndReturnsNewUUID() {
        var result = recipeController.createRecipe(new Recipe("test"));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result);
    }

    @Test
    void createsRecipeAndExists() {
        assertNotNull(recipeController.createRecipe(new Recipe("test")));
        verify(repo, times(1)).save(new Recipe("test"));
    }

    @Test
    void returnsAllRecipesWhenGettingAll() {
        Recipe recipe1 = new Recipe("Recipe 1");
        Recipe recipe2 = new Recipe("Recipe 2");
        List<Recipe> recipes = List.of(recipe1, recipe2);

        when(repo.findAll()).thenReturn(recipes);

        ResponseEntity<List<Recipe>> result = recipeController.getAllRecipes();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(recipes, result.getBody());
        verify(repo, times(1)).findAll();
    }

    @Test
    void returnsRecipeWhenFoundByID() {
        Recipe recipe = new Recipe("Test Recipe");
        var id = recipe.getRecipeId();

        when(repo.findById(id)).thenReturn(Optional.of(recipe));

        ResponseEntity<Recipe> result = recipeController.getRecipe(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(recipe, result.getBody());
        verify(repo, times(1)).findById(id);
    }

    @Test
    void returns404WhenRecipeDoesNotExist() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Recipe> result = recipeController.getRecipe(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(repo, times(1)).findById(1L);
    }

    @Test
    void updatesRecipeWhenReplacingExistingOne() {
        Recipe recipe = new Recipe("Updated Recipe");
        var id = recipe.getRecipeId();

        when(repo.findById(id)).thenReturn(Optional.of(recipe));

        ResponseEntity<Long> result = recipeController.replaceRecipe(
            id,
            recipe
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).save(recipe);
    }

    @Test
    void returns404WhenTryingToReplaceNonExistentRecipe() {
        Recipe recipe = new Recipe("Updated Recipe");

        when(repo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Long> result = recipeController.replaceRecipe(
            1L,
            recipe
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(0)).save(recipe);
    }

    @Test
    void removesRecipeWhenDeletingExistingOne() {
        Recipe recipe = new Recipe("Test Recipe");
        var id = recipe.getRecipeId();

        when(repo.findById(id)).thenReturn(Optional.of(recipe));

        ResponseEntity<Long> result = recipeController.deleteRecipe(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(repo, times(1)).findById(id);
        verify(repo, times(1)).deleteById(id);
    }

    @Test
    void returns404WhenTryingToDeleteNonExistentRecipe() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Long> result = recipeController.deleteRecipe(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(0)).deleteById(1L);
    }
}
