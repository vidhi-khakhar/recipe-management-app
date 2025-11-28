package server.api;

import commons.Recipe;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.RecipeRepository;
import server.RecipeService;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeService rService;

    public RecipeController(RecipeService service) {
        this.rService = service;
    }

    /**
     * Defines endpoint for creating recipes in the repository
     * @param recipe the recipe specified in the POST request to add to the repo
     * @return the id of the entity added
     */
    @PostMapping
    public ResponseEntity<Long> createRecipe(@RequestBody Recipe recipe) {
        this.rService.createRecipe(recipe);

        return ResponseEntity.ok(recipe.getRecipeId());
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
     * Defines endpoint for replacing a recipe in the repository
     * @param id the id of the recipe to replace
     * @param recipe the recipe to replace with
     * @return id of the recipe that was replaced, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Long> replaceRecipe(
        @PathVariable Long id,
        @RequestBody Recipe recipe
    ) {
        try {
            this.rService.getRecipeById(id);
            this.rService.updateRecipe(id, recipe);
            return ResponseEntity.ok(id);
        }
        catch (NoSuchElementException e) {
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
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
