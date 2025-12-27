package server.api;

import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.RecipeIngredientService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/recipe-ingredients")
public class RecipeIngredientController {

    private final RecipeIngredientService recipeIngredientService;

    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    /**
     * Creates a new RecipeIngredient.
     */
    @PostMapping
    public ResponseEntity<RecipeIngredient> createRecipeIngredient(
            @RequestBody RecipeIngredient recipeIngredient) {

        RecipeIngredient saved =
                recipeIngredientService.createRecipeIngredient(recipeIngredient);

        return ResponseEntity.status(201).body(saved);
    }

    /**
     * Gets a RecipeIngredient by composite key.
     */
    @GetMapping
    public ResponseEntity<RecipeIngredient> getRecipeIngredient(
            @RequestParam Long recipeId,
            @RequestParam Long ingredientId) {

        RecipeIngredientKey key =
                new RecipeIngredientKey(recipeId, ingredientId);

        try {
            return ResponseEntity.ok(
                    recipeIngredientService.getRecipeIngredient(key)
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Gets all RecipeIngredients.
     */
    @GetMapping("/all")
    public ResponseEntity<List<RecipeIngredient>> getAllRecipeIngredients() {
        return ResponseEntity.ok(
                recipeIngredientService.getAllRecipeIngredients()
        );
    }

    /**
     * Updates amount/unit or informal value of a RecipeIngredient.
     */
    @PutMapping
    public ResponseEntity<Void> updateRecipeIngredient(
            @RequestParam Long recipeId,
            @RequestParam Long ingredientId,
            @RequestBody RecipeIngredient updated) {

        RecipeIngredientKey key =
                new RecipeIngredientKey(recipeId, ingredientId);

        try {
            recipeIngredientService.updateRecipeIngredient(key, updated);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Deletes a RecipeIngredient.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteRecipeIngredient(
            @RequestParam Long recipeId,
            @RequestParam Long ingredientId) {

        RecipeIngredientKey key =
                new RecipeIngredientKey(recipeId, ingredientId);

        try {
            recipeIngredientService.deleteRecipeIngredient(key);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
