package server;

import commons.Ingredient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import server.database.IngredientRepository;

/**
 * @DataJpaTest is specifically designed for testing database-layer functionality.
 * It wraps each test in a transaction and rolls it back,
 * so that the tests do not interfere with each other.
 */
@DataJpaTest(showSql = true)
class IngredientRepositoryTest {
    @Autowired
    private IngredientRepository ingredientRepository;
    private Ingredient testIngredient1;
    private Ingredient testIngredient2;

    @BeforeEach
    void setUp() {
        testIngredient1 = new Ingredient("Sarmale", 1.0, 1.0, 1.0);
        testIngredient2 = new Ingredient("Tocanita", 2.0, 2.0, 2.0);

        ingredientRepository.save(testIngredient1);
        ingredientRepository.save(testIngredient2);
    }

    @Test
    void testFindAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();

        assertEquals(2, ingredients.size());
    }

    @Test
    void testFindById() {
        Ingredient found = ingredientRepository.findById(testIngredient1.getId()).get();

        assertEquals(testIngredient1.getId(), found.getId());
    }

    @Test
    void testDeleteIngredient() {
        ingredientRepository.deleteById(testIngredient1.getId());
        List<Ingredient> ingredients = ingredientRepository.findAll();

        assertEquals(1, ingredients.size());
    }

    @Test
    void testExistsById() {
        boolean exists = ingredientRepository.existsById(testIngredient1.getId());
        assertEquals(true, exists);
    }

    @Test
    void testDeleteAll() {
        ingredientRepository.deleteAll();
        List<Ingredient> ingredients = ingredientRepository.findAll();

        assertEquals(0, ingredients.size());
    }

    @Test
    void testSave() {
        Ingredient savedIngredient = ingredientRepository.save(testIngredient1);

        assertEquals(testIngredient1.getId(), savedIngredient.getId());
    }
    
}