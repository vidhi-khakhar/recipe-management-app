package server.api;

import commons.Recipe;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.RecipeRepository;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final RecipeRepository repo;

    public RecipeController(RecipeRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Long> createRecipe(@RequestBody Recipe recipe) {
        this.repo.save(recipe);

        return ResponseEntity.ok(recipe.getRecipeId());
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(this.repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(this.repo.findById(id).get());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> replaceRecipe(
        @PathVariable Long id,
        @RequestBody Recipe recipe
    ) {
        if (this.repo.findById(id).isPresent()) {
            this.repo.save(recipe);
            return ResponseEntity.ok(id);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteRecipe(@PathVariable Long id) {
        if (this.repo.findById(id).isPresent()) {
            this.repo.deleteById(id);
            return ResponseEntity.ok(id);
        }
        return ResponseEntity.notFound().build();
    }
}
