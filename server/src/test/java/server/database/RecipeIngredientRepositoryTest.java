package server.database;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import server.database.RecipeRepository;
import server.database.IngredientRepository;

@DataJpaTest(showSql = true)
class RecipeIngredientRepositoryTest {

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    private Recipe testRecipe;
    private Ingredient testIngredient;
    private RecipeIngredient testRecipeIngredient;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe("Test Recipe");
        recipeRepository.save(testRecipe);

        testIngredient = new Ingredient("Test Ingredient", 100.0, 50.0, 25.0);
        ingredientRepository.save(testIngredient);

        testRecipeIngredient = RecipeIngredient.formal(
                testRecipe,
                testIngredient,
                2.0,
                commons.Unit.G
        );
        recipeIngredientRepository.save(testRecipeIngredient);
    }

    @Test
    void testFindAll() {
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAll();
        assertEquals(1, recipeIngredients.size());
    }

    @Test
    void testFindById() {
        RecipeIngredientKey key = testRecipeIngredient.getId();
        RecipeIngredient found = recipeIngredientRepository.findById(key).get();

        assertEquals(key, found.getId());
    }

    @Test
    void testExistsById() {
        RecipeIngredientKey key = testRecipeIngredient.getId();
        boolean exists = recipeIngredientRepository.existsById(key);
        assertEquals(true, exists);
    }

    @Test
    void testDeleteById() {
        RecipeIngredientKey key = testRecipeIngredient.getId();
        recipeIngredientRepository.deleteById(key);
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAll();

        assertEquals(0, recipeIngredients.size());
    }

    @Test
    void testDeleteAll() {
        recipeIngredientRepository.deleteAll();
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAll();

        assertEquals(0, recipeIngredients.size());
    }

    @Test
    void testSave() {
        RecipeIngredientKey key = testRecipeIngredient.getId();
        RecipeIngredient saved = recipeIngredientRepository.save(testRecipeIngredient);

        assertEquals(key, saved.getId());
        assertNotNull(saved.getRecipe());
        assertNotNull(saved.getIngredient());
    }

    @Test
    void testDeleteByIngredientId() {
        Long ingredientId = testIngredient.getId();
        recipeIngredientRepository.deleteByIngredientId(ingredientId);

        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAll();
        assertEquals(0, recipeIngredients.size());
    }
}
