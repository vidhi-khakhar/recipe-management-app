package server.api;

import commons.Ingredient;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.IngredientService;

@RestController
@RequestMapping("/api/vi/ingredients")
public class IngredientController {
    private final IngredientService iService;

    public IngredientController(IngredientService service){
        this.iService = service;
    }

    /**
     * Defines endpoint for creating ingredients in the repository
     * @param ingredient the ingredient specified in the POST request to add to the repo
     * @return the id of the entity added
     */
    @PostMapping
    public ResponseEntity<Long> create(@RequestBody Ingredient ingredient){
        this.iService.createIngredient(ingredient);

        return ResponseEntity.ok(ingredient.getId());
    }

    /**
     * Defines endpoint for getting all ingredients from the repository
     * @return all ingredients in the repository as a list of Ingredient objects
     */
    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients(){

        return ResponseEntity.ok(this.iService.getAllRecipes());
    }

    /**
     * Defines endpoint for getting an ingrendient by its id from the repository
     * @param id the id of the ingredient to get from the repo
     * @return the ingredient with the specified id, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(this.iService.getIngredientById());
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
      try{
          this.iService.updateIngredient(id, ingredient);
          return ResponseEntity.ok(id);
      }catch (NoSuchElementException e){
          return ResponseEntity.notFound().build();
      }
    }

    /**
     * Defines endpoint for deleting an ingredient from the repository
     * @param id the id of the ingredient to delete
     * @return The id of the ingredient that was deleted, or 404 if not found
     */
    @DeleteMapping("/id")
    public ResponseEntity<Long> deleteIngredient(@PathVariable Long id){
        try{
            this.iService.deleteIngredientById(id);
            return ResponseEntity.ok(id);
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

}
