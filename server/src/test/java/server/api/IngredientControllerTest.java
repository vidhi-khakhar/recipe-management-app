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
        Ingredient ingredient = new Ingredient("test", 1.0, 1.0, 1.0);
        ingredient.setId(10L);

        when(iService.createIngredient(same(ingredient))).thenReturn(ingredient);

        var result = ingredientController.createIngredient(ingredient);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result);
    }

    @Test
    void createsIngredientAndExists() {
        Ingredient ing = new Ingredient("test", 1.0, 1.0, 1.0);
        ing.setId(10L);

        when(iService.createIngredient(same(ing))).thenReturn(ing);
        ingredientController.createIngredient(ing);

        verify(iService).createIngredient(same(ing));
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
        Long id = 1L;
        Ingredient updated = new Ingredient("Updated Ingredient", 1.0, 1.0, 1.0);

        when(iService.getIngredientById(id))
                .thenReturn(new Ingredient("Existing", 1.0, 1.0, 1.0));

        ResponseEntity<Long> result = ingredientController.updateIngredient(id, updated);

        assertEquals(HttpStatus.OK, result.getStatusCode());

        verify(iService).getIngredientById(id);
        verify(iService).updateIngredient(id, updated);
    }

    @Test
    void returns404WhenTryingToReplaceNonExistentIngredient() {
        Long id = 1L;
        Ingredient updated = new Ingredient("Updated Ingredient", 1.0, 1.0, 1.0);

        when(iService.getIngredientById(id)).thenThrow(new NoSuchElementException("not found"));

        ResponseEntity<Long> result = ingredientController.updateIngredient(id, updated);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(iService, times(1)).getIngredientById(id);

        verify(iService, never()).updateIngredient(anyLong(), any(Ingredient.class));
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

