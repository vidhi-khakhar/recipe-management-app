package server.service;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.IngredientRepository;
import server.database.RecipeIngredientRepository;
import server.database.RecipeRepository;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeIngredientServiceTest {

    @Mock
    private RecipeIngredientRepository rIngRepo;

    @Mock
    private RecipeRepository recipeRepo;

    @Mock
    private IngredientRepository ingredientRepo;

    @InjectMocks
    private RecipeIngredientService rIngService;

    private Recipe recipe;
    private Ingredient ingredient;
    private RecipeIngredient rIng;
    private RecipeIngredientKey id;

    @BeforeEach
    void setup() {
        recipe = new Recipe("Bors", "ro");
        ingredient = new Ingredient("ceapa", 10, 10, 10);
        rIng = RecipeIngredient.formal(recipe, ingredient, 500.0, Unit.G);
        id = rIng.getId();
    }

    /**
     * Tests that a RecipeIngredient is correctly created and saved to the database.
     */
    @Test
    void createRecipeIngredient() {
        when(ingredientRepo.findById(any()))
                .thenReturn(Optional.of(ingredient));
        when(recipeRepo.findById(any()))
                .thenReturn(Optional.of(recipe));

        rIngService.createRecipeIngredient(rIng);

        verify(rIngRepo, times(1)).save(rIng);
    }

    /**
     * Tests that a RecipeIngredient can be retrieved by its ID and that the correct ingredient is returned.
     */
    @Test
    void getRecipeIngredient() {
        when(rIngRepo.findById(id))
                .thenReturn(Optional.of(rIng));

        RecipeIngredient result = rIngService.getRecipeIngredient(id);

        assertEquals(ingredient, result.getIngredient());
    }

    /**
     * Tests that all RecipeIngredient objects from the database are correctly returned.
     */
    @Test
    void getAllRecipeIngredients() {
        RecipeIngredient rIng1 =
                RecipeIngredient.formal(recipe, ingredient, 500.0, Unit.G);
        RecipeIngredient rIng2 =
                RecipeIngredient.informal(recipe, ingredient, "a pinch");

        when(rIngRepo.findAll())
                .thenReturn(Arrays.asList(rIng1, rIng2));

        assertEquals(2, rIngService.getAllRecipeIngredients().size());
    }

    /**
     * Tests that an exception is thrown if the ingredient does not exist in the database, and nothing is saved.
     */
    @Test
    void createRecipeIngredient_ingredientNotFound_throwsException() {
        when(ingredientRepo.findById(any()))
                .thenReturn(Optional.empty());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> rIngService.createRecipeIngredient(rIng)
        );

        assertEquals("Ingredient not found", exception.getMessage());
        verify(rIngRepo, never()).save(any());
    }

    /**
     * Tests that an exception is thrown if the recipe does not exist in the database, and nothing is saved.
     */
    @Test
    void createRecipeIngredient_recipeNotFound_throwsException() {
        when(ingredientRepo.findById(any()))
                .thenReturn(Optional.of(ingredient));
        when(recipeRepo.findById(any()))
                .thenReturn(Optional.empty());

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> rIngService.createRecipeIngredient(rIng)
        );

        assertEquals("Recipe not found", exception.getMessage());
        verify(rIngRepo, never()).save(any());
    }

    /**
     * Tests that an exception is thrown if a RecipeIngredient with a given ID does not exist.
     */
    @Test
    void getRecipeIngredient_notFound_throwsException() {
        when(rIngRepo.findById(id))
                .thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                NoSuchElementException.class,
                () -> rIngService.getRecipeIngredient(id)
        );
    }

    /**
     * Tests that only RecipeIngredient objects belonging to a specific recipe are returned (filters by recipeId).
     */
    @Test
    void getRecipeIngredients_filtersByRecipeId() {
        // Sets an explicit ID
        recipe.setRecipeId(1L);
        Recipe otherRecipe = new Recipe("Soup", "en");
        otherRecipe.setRecipeId(2L);

        RecipeIngredient ri1 = RecipeIngredient.formal(recipe, ingredient, 100.0, Unit.G);
        ri1.setId(new RecipeIngredientKey(recipe.getRecipeId(), ingredient.getId()));

        RecipeIngredient ri2 = RecipeIngredient.formal(otherRecipe, ingredient, 200.0, Unit.G);
        ri2.setId(new RecipeIngredientKey(otherRecipe.getRecipeId(), ingredient.getId()));
    }

}
