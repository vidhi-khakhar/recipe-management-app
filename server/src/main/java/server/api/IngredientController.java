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

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody Ingredient ingredient){
        Ingredient i = iService.createIngredient(ingredient);

        return ResponseEntity.ok(i.getId());
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients(){

        return ResponseEntity.ok(this.iService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(@PathVariable Long id){
        try {
            return ResponseEntity.ok(this.iService.getIngredientById());
        }catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
    }

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
