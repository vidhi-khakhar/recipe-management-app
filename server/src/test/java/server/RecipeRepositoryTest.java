package server;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(showSql = true)
class RecipeRepositoryTest {
    @Autowired
    private server.RecipeRepository recipeRepository;

    @BeforeEach
    void setUp() {
        Recipe testRecipe1 = new Recipe("Sarmale");
        Recipe testRecipe2 = new Recipe("Tocanita");

        recipeRepository.save(testRecipe1);
        recipeRepository.save(testRecipe2);
    }
}