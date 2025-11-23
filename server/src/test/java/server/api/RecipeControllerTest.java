package server.api;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RecipeControllerTest {

    @Test
    void createsRecipeAndReturnsNewUUID() {
        HashMap<UUID, String> repo = new HashMap<>();
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> response = controller.createRecipe("Test Recipe");

        assertNotNull(response.getBody());
    }

    @Test
    void createsRecipeAndExists() {
        HashMap<UUID, String> repo = new HashMap<>();
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> response = controller.createRecipe("Test Recipe");

        assertEquals("Test Recipe", repo.get(response.getBody()));
    }

    @Test
    void returnsAllRecipesWhenGettingAll() {
        HashMap<UUID, String> repo = new HashMap<>();
        repo.put(UUID.randomUUID(), "Recipe 1");
        repo.put(UUID.randomUUID(), "Recipe 2");
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<HashMap<UUID, String>> response = controller.getAllRecipes();

        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void returnsRecipeWhenFoundByID() {
        HashMap<UUID, String> repo = new HashMap<>();
        UUID id = UUID.randomUUID();
        repo.put(id, "Test Recipe");
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<String> response = controller.getRecipe(id);

        assertEquals("Test Recipe", response.getBody());
    }

    @Test
    void returns404WhenRecipeDoesNotExist() {
        HashMap<UUID, String> repo = new HashMap<>();
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<String> response = controller.getRecipe(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatesRecipeWhenReplacingExistingOne() {
        HashMap<UUID, String> repo = new HashMap<>();
        UUID id = UUID.randomUUID();
        repo.put(id, "Old Recipe");
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> _ = controller.replaceRecipe(id, "New Recipe");

        assertEquals("New Recipe", repo.get(id));
    }

    @Test
    void returns404WhenTryingToReplaceNonExistentRecipe() {
        HashMap<UUID, String> repo = new HashMap<>();
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> response = controller.replaceRecipe(UUID.randomUUID(), "New Recipe");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void removesRecipeWhenDeletingExistingOne() {
        HashMap<UUID, String> repo = new HashMap<>();
        UUID id = UUID.randomUUID();
        repo.put(id, "Recipe to delete");
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> _ = controller.deleteRecipe(id);

        assertFalse(repo.containsKey(id));
    }

    @Test
    void returns404WhenTryingToDeleteNonExistentRecipe() {
        HashMap<UUID, String> repo = new HashMap<>();
        RecipeController controller = new RecipeController(repo);

        ResponseEntity<UUID> response = controller.deleteRecipe(UUID.randomUUID());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}