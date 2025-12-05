package server.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import commons.Ingredient;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import server.service.IngredientService;

@ExtendWith(MockitoExtension.class)
class IngredientControllerTest {

    @Mock
    private IngredientService iService;

    @InjectMocks
    private IngredientController ingredientController;

    @Test
    void createsIngredientAndReturnsNewUUID() {
        var result = ingredientController.create(new Ingredient("test", 1.0, 1.0, 1.0));

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result);
    }

    @Test
    void createsIngredientAndExists() {
        assertNotNull(ingredientController.create(new Ingredient("test",1.0, 1.0, 1.0)));
        verify(iService, times(1)).createIngredient(new Ingredient("test",1.0, 1.0, 1.0));
    }

    @Test
    void returnsAllIngredientsWhenGettingAll() {
    Ingredient ingredient1 = new Ingredient("Ingredient 1", 1.0, 1.0, 1.0);
        Ingredient ingredient2 = new Ingredient("Ingredient 2", 1.0, 1.0, 1.0);
        List<Ingredient> ingredients = List.of(ingredient1, ingredient2);

        when(iService.getAllIngredients()).thenReturn(ingredients);

        ResponseEntity<List<Ingredient>> result = ingredientController.getAllIngredients();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(ingredients, result.getBody());
        verify(iService, times(1)).getAllIngredients();
    }

    @Test
    void returnsIngredientWhenFoundByID() {
        Ingredient ingredient = new Ingredient("Test Ingredient", 1.0, 1.0, 1.0);
        var id = ingredient.getId();

        when(iService.getIngredientById(id)).thenReturn(ingredient);

        ResponseEntity<Ingredient> result = ingredientController.getIngredientById(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(ingredient, result.getBody());
        verify(iService, times(1)).getIngredientById(id);
    }

    @Test
    void returns404WhenIngredientDoesNotExist() {
        when(iService.getIngredientById(1L)).thenThrow(
                new NoSuchElementException("")
        );

        ResponseEntity<Ingredient> result = ingredientController.getIngredientById(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());
        verify(iService, times(1)).getIngredientById(1L);
    }

    @Test
    void updatesIngredientWhenReplacingExistingOne() {
        Ingredient ingredient = new Ingredient("Updated Ingredient", 1.0, 1.0, 1.0);
        var id = ingredient.getId();

        ResponseEntity<Long> result = ingredientController.updateIngredient(
                id,
                ingredient
        );

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(iService, times(1)).updateIngredient(id, ingredient);
    }

    @Test
    void returns404WhenTryingToReplaceNonExistentIngredient() {
        Ingredient ingredient = new Ingredient("Updated Ingredient", 1.0, 1.0, 1.0);

        doThrow(new NoSuchElementException("Ingredient not found"))
                .when(iService)
                .updateIngredient(eq(1L), any(Ingredient.class));

        ResponseEntity<Long> result = ingredientController.updateIngredient(
                1L,
                ingredient
        );

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(iService, times(1)).updateIngredient(eq(1L), any(Ingredient.class));
    }

    @Test
    void removesIngredientWhenDeletingExistingOne() {
        Ingredient ingredient = new Ingredient("Test Ingredient", 1.0, 1.0, 1.0);
        var id = ingredient.getId();

        ResponseEntity<Long> result = ingredientController.deleteIngredient(id);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(id, result.getBody());
        verify(iService, times(1)).deleteIngredientById(id);
    }

    @Test
    void returns404WhenTryingToDeleteNonExistentIngredient() {
        doThrow(new NoSuchElementException("Ingredient not found"))
                .when(iService)
                .deleteIngredientById(1L);

        ResponseEntity<Long> result = ingredientController.deleteIngredient(1L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(iService, times(1)).deleteIngredientById(1L);
    }
}

