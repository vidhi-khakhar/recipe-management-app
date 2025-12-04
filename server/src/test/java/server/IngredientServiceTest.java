package server;


import commons.Ingredient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private server.database.IngredientRepository ingredientRepository;

    @InjectMocks
    private server.service.IngredientService ingredientService;

    @Test
    void getIngredientById() {
        Ingredient ingredient = new Ingredient(123L, "Mozzarella", 18.0, 19.0, 1.0);
        Long id = ingredient.getId();

        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredient));

        assertEquals(123L, ingredientService.getIngredientById(id).getId());
    }

    @Test
    void createIngredient() {
        Ingredient ingredient = new Ingredient(123L, "Mozzarella", 18.0, 19.0, 1.0);

        assertNotNull(ingredient.getId());
        assertNotNull(ingredient.getName());
    }

    @Test
    void deleteIngredientById() {
        Ingredient ingredient = new Ingredient(123L, "Mozzarella", 18.0, 19.0, 1.0);
        Long id = ingredient.getId();

        when(ingredientRepository.existsById(id)).thenReturn(true);
        ingredientService.deleteIngredientById(id);

        verify(ingredientRepository, times(1)).deleteById(id);
    }

    @Test
    void updateIngredient() {
        Ingredient ingredientToUpdate = new Ingredient(123L, "Mozzarella", 18.0, 19.0, 1.0);
        Long id = ingredientToUpdate.getId();
        when(ingredientRepository.findById(id)).thenReturn(Optional.of(ingredientToUpdate));

        Ingredient updatedIngredient = new Ingredient(123L, "Buffel Mozzarella", 13.0, 23.0, 0.6);
        ingredientService.updateIngredient(id, updatedIngredient);

        verify(ingredientRepository, times(1)).save(ingredientToUpdate);
        assertEquals("Buffel Mozzarella", ingredientService.getIngredientById(id).getName());
        assertEquals(13, ingredientService.getIngredientById(id).getProteinPer100g());
        assertEquals(23, ingredientService.getIngredientById(id).getFatPer100g());
        assertEquals(0.6, ingredientService.getIngredientById(id).getCarbsPer100g());
    }



}
