package server;

import commons.Recipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @DataJpaTest is specifically designed for testing database-layer functionality.
 * It wraps each test in a transaction and rolls it back,
 * so that the tests do not interfere with each other.
 */
@DataJpaTest(showSql = true)
class RecipeRepositoryTest {
    @Autowired
    private server.RecipeRepository recipeRepository;
    private Recipe testRecipe1;
    private Recipe testRecipe2;

    @BeforeEach
    void setUp() {
        testRecipe1 = new Recipe("Sarmale");
        testRecipe2 = new Recipe("Tocanita");

        recipeRepository.save(testRecipe1);
        recipeRepository.save(testRecipe2);
    }

    @Test
    void testFindAll() {
        List<Recipe> recipes = recipeRepository.findAll();

        assertEquals(2, recipes.size());
    }

    @Test
    void testFindById() {
        Recipe found = recipeRepository.findById(testRecipe1.getRecipeId()).get();

        assertEquals(testRecipe1.getRecipeId(), found.getRecipeId());
    }

    @Test
    void testDeleteRecipe() {
        recipeRepository.deleteById(testRecipe1.getRecipeId());
        List<Recipe> recipes = recipeRepository.findAll();

        assertEquals(1, recipes.size());
    }

    @Test
    void testExistsById() {
        boolean exists = recipeRepository.existsById(testRecipe1.getRecipeId());
        assertEquals(true, exists);
    }

    @Test
    void testDeleteAll() {
        recipeRepository.deleteAll();
        List<Recipe> recipes = recipeRepository.findAll();

        assertEquals(0, recipes.size());
    }

    @Test
    void testSave() {
        Recipe savedRecipe = recipeRepository.save(testRecipe1);

        assertEquals(testRecipe1.getRecipeId(), savedRecipe.getRecipeId());
    }
}