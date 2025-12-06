package server.api;

import commons.Ingredient;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.IngredientService;

@RestController
@RequestMapping("/api/v1/ingredients")
public class IngredientController {
    private final IngredientService iService;

    public IngredientController(IngredientService service){
        this.iService = service;
    }

    /**
     * Method that checks that the ingredient passed a parameter is valid.
     * @param ingredient the ingredient to validate
     */
    private static void validateIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient cannot be null");
        }

        String name = ingredient.getName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Ingredient name cannot be null or blank");
        }
        if (ingredient.getProteinPer100g() < 0) {
            throw new IllegalArgumentException("Protein cannot be negative");
        }
        if (ingredient.getFatPer100g() < 0) {
            throw new IllegalArgumentException("Fat cannot be negative");
        }
        if (ingredient.getCarbsPer100g() < 0) {
            throw new IllegalArgumentException("Carbs cannot be negative");
        }
    }

    /**
     * Defines endpoint for creating ingredients in the repository
     * @param ingredient the ingredient specified in the POST request to add to the repo
     * @return the id of the entity added
     */
    @PostMapping
    public ResponseEntity<Long> createIngredient(@RequestBody Ingredient ingredient){
        validateIngredient(ingredient);

        this.iService.createIngredient(ingredient);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredient.getId());
    }

    /**
     * Defines endpoint for getting all ingredients from the repository
     * @return all ingredients in the repository as a list of Ingredient objects
     */
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients(){

        return ResponseEntity.ok(this.iService.getAllIngredients());
    }

    /**
     * Defines endpoint for getting an ingrendient by its id from the repository
     * @param id the id of the ingredient to get from the repo
     * @return the ingredient with the specified id, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(this.iService.getIngredientById(id));
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Defines endpoint for replacing an ingredient in the repository
     * @param id the id of the ingredient to replace
     * @param ingredient the ingredient to replace with
     * @return id of the ingredient that was replaced, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateIngredient(
            @PathVariable Long id,
            @RequestBody Ingredient ingredient
    ){
        validateIngredient(ingredient);

      try{
          iService.getIngredientById(id);
      }catch (NoSuchElementException e){
          return ResponseEntity.notFound().build();
      }

      iService.updateIngredient(id, ingredient);
      return ResponseEntity.ok(ingredient.getId());
    }

    /**
     * Defines endpoint for deleting an ingredient from the repository
     * @param id the id of the ingredient to delete
     * @return The id of the ingredient that was deleted, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteIngredient(@PathVariable Long id){
        try{
            this.iService.deleteIngredientById(id);
            return ResponseEntity.ok(id);
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

}
