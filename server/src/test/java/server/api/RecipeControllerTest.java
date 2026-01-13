package server.api;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import commons.Recipe;
import server.service.RecipeService;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService rService;

    @InjectMocks
    private RecipeController recipeController;

    @Test
    void createsRecipeAndReturnsNewUUID() {

        Recipe recipe = new Recipe("test");

        var result = recipeController.createRecipe(recipe);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(rService, times(1)).createRecipe(recipe);
    }

    @Test
    void createsRecipeAndExists() {

        Recipe recipe = new Recipe("test");

        var result = recipeController.createRecipe(recipe);

        assertNotNull(result);
        verify(rService, times(1)).createRecipe(recipe);
    }

    @Test
    void returnsAllRecipesWhenGettingAll() {
        Recipe recipe1 = new Recipe("Recipe 1");
        recipe1.setRecipeId(1L);
        Recipe recipe2 = new Recipe("Recipe 2");
        recipe2.setRecipeId(2L);
        List<Recipe> recipes = List.of(recipe1, recipe2);

        when(rService.getAllRecipes()).thenReturn(recipes);

        ResponseEntity<List<Recipe>> result = recipeController.getAllRecipes();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(recipes, result.getBody());
        verify(rService, times(1)).getAllRecipes();
    }

    @Test
    void returnsRecipeWhenFoundByID() {
        Recipe recipe = new Recipe("Test Recipe");
        recipe.setRecipeId(1L);
        var id = recipe.getRecipeId();

        when(rService.getRecipeById(id)).thenReturn(recipe);

        ResponseEntity<Recipe> result = recipeController.getRecipe(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(recipe, result.getBody());
        verify(rService, times(1)).getRecipeById(id);
    }

    @Test
    void returns404WhenRecipeDoesNotExist() {
        when(rService.getRecipeById(1L)).thenThrow(
            new NoSuchElementException("")
        );

        ResponseEntity<Recipe> result = recipeController.getRecipe(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(rService, times(1)).getRecipeById(1L);
    }

    @Test
    void updatesRecipeWhenReplacingExistingOne() {
        Recipe recipe = new Recipe("Updated Recipe");
        var id = recipe.getRecipeId();

        ResponseEntity<Long> result = recipeController.updateRecipe(id, recipe);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(rService, times(1)).updateRecipe(id, recipe);
    }

    @Test
    void returns404WhenTryingToReplaceNonExistentRecipe() {
        Recipe recipe = new Recipe("Updated Recipe");

        doThrow(new NoSuchElementException("Recipe not found"))
            .when(rService)
            .updateRecipe(eq(1L), any(Recipe.class));

        ResponseEntity<Long> result = recipeController.updateRecipe(1L, recipe);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(rService, times(1)).updateRecipe(eq(1L), any(Recipe.class));
    }

    @Test
    void removesRecipeWhenDeletingExistingOne() {
        Recipe recipe = new Recipe("Test Recipe");
        recipe.setRecipeId(1L);
        var id = recipe.getRecipeId();

        ResponseEntity<Long> result = recipeController.deleteRecipe(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(rService, times(1)).deleteRecipeById(id);
    }

    @Test
    void returns404WhenTryingToDeleteNonExistentRecipe() {
        doThrow(new NoSuchElementException("Recipe not found"))
            .when(rService)
            .deleteRecipeById(1L);

        ResponseEntity<Long> result = recipeController.deleteRecipe(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(rService, times(1)).deleteRecipeById(1L);
    }
}
