package server.api;

import commons.Recipe;
import java.util.List;
import java.util.NoSuchElementException;

import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.RecipeService;
import server.service.RecipeIngredientService;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService rService;
    private final RecipeIngredientService recipeIngService;

    public RecipeController(RecipeService service,
                            RecipeIngredientService recipeIngredientService) {
        this.rService = service;
        this.recipeIngService = recipeIngredientService;
    }

    /**
     * Defines endpoint for creating recipes in the repository
     * @param recipe the recipe specified in the POST request to add to the repo
     * @return the id of the entity added
     */
    @PostMapping
    public ResponseEntity<Long> createRecipe(@RequestBody Recipe recipe) {
        this.rService.createRecipe(recipe);

        return ResponseEntity.status(201).body(recipe.getRecipeId());
    }

    /**
     * Defines endpoint for getting all recipes from the repository
     * @return all recipes in the repository as a list of Recipe objects
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(this.rService.getAllRecipes());
    }

    /**
     * Defines endpoint for getting a recipe by its id from the repository
     * @param id the id of the recipe to get from the repo
     * @return the recipe with the specified id, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.rService.getRecipeById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Defines endpoint for updating a recipe in the repository
     * @param id the id of the recipe to update
     * @param recipe the recipe to update with
     * @return id of the recipe that was updated, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateRecipe(
        @PathVariable Long id,
        @RequestBody Recipe recipe
    ) {
        try {
            this.rService.updateRecipe(id, recipe);
            return ResponseEntity.ok(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Defines endpoint for deleting a recipe from the repository
     * @param id the id of the recipe to delete
     * @return The id of the recipe that was deleted, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteRecipe(@PathVariable Long id) {
        try {
            this.rService.deleteRecipeById(id);
            return ResponseEntity.ok(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RECIPE INGREDIENT ENDPOINTS
    /**
     * Defines endpoint for creating recipeIngredients in the database
     * @param recipeIngredient the ingredient to be added to recipe
     *                         with all additional info it entails
     * @return the composite id (recipeId + ingredientId) of the entity added
     */
    @PostMapping("/ingredients")
    public ResponseEntity<RecipeIngredientKey> createRecipeIngredient(@RequestBody RecipeIngredient recipeIngredient) {
        this.recipeIngService.createRecipeIngredient(recipeIngredient);
        return ResponseEntity.status(201).body(recipeIngredient.getId());
    }

    /**
     * Defines endpoint for getting all recipeIngredients for a recipe.
     * @return all the saved recipeIngredients
     */
    @GetMapping("/{recipeId}/ingredients")
    public ResponseEntity<List<RecipeIngredient>> getRecipeIngredients(@PathVariable Long recipeId) {
        return ResponseEntity.ok(this.recipeIngService.getRecipeIngredients(recipeId));
    }

    /**
     * Defines endpoint for getting all recipeIngredients from database.
     */
    @GetMapping("/ingredients")
    public ResponseEntity<List<RecipeIngredient>> getAllRecipeIngredients() {
        return ResponseEntity.ok(this.recipeIngService.getAllRecipeIngredients());
    }

    /**
     * Defines endpoint for deleting a recipeIngredient from the database.
     * @param recipeId the id of the recipe
     * @param ingredientId the id of the ingredient
     * @return the id of the deleted recipeIngredient
     */
    @DeleteMapping("/ingredients/{recipeId}/{ingredientId}")
    public ResponseEntity<RecipeIngredientKey> deleteRecipeIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId) {
        RecipeIngredientKey id = new RecipeIngredientKey(recipeId, ingredientId);

        try {
            this.recipeIngService.deleteRecipeIngredient(id);
            return ResponseEntity.ok(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Defines endpoint for updating an ingredient inside a recipe.
     * @param recipeId the id of the recipe
     * @param ingredientId the id of the ingredient
     * @param recipeIngredient the recipeIngredient to update with
     * @return id of the recipeIngredient that was updated
     */
    @PutMapping("/ingredients/{recipeId}/{ingredientId}")
    public ResponseEntity<RecipeIngredientKey> updateRecipeIngredient(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @RequestBody RecipeIngredient recipeIngredient
    ) {
        RecipeIngredientKey id = new RecipeIngredientKey(recipeId, ingredientId);
        try {
            this.recipeIngService.updateRecipeIngredient(id, recipeIngredient);
            return ResponseEntity.ok(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
