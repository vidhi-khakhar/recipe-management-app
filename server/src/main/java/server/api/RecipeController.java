package server.api;

import java.util.HashMap;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recipes")
public class RecipeController {

    private final HashMap<UUID, String> repo;

    public RecipeController(HashMap<UUID, String> repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<UUID> createRecipe(@RequestBody String recipe) {
        System.out.println("Add recipe: " + recipe);

        var uuid = UUID.randomUUID();

        this.repo.put(uuid, recipe);

        return ResponseEntity.ok(uuid);
    }

    @GetMapping
    public ResponseEntity<HashMap<UUID, String>> getAllRecipes() {
        return ResponseEntity.ok(this.repo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getRecipe(@PathVariable UUID id) {
        if (this.repo.containsKey(id)) {
            return ResponseEntity.ok(this.repo.get(id));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> replaceRecipe(
        @PathVariable UUID id,
        @RequestBody String recipe
    ) {
        if (this.repo.containsKey(id)) {
            this.repo.put(id, recipe);
            return ResponseEntity.ok(id);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UUID> deleteRecipe(@PathVariable UUID id) {
        if (this.repo.containsKey(id)) {
            this.repo.remove(id);
            return ResponseEntity.ok(id);
        }
        return ResponseEntity.notFound().build();
    }
}
