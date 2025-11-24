package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class RecipeService {
    private RecipeRepository recipeRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Creates a new recipe.
     * @param recipe recipe to be added to database
     * @return the created recipe
     */
    public Recipe createRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    /**
     * Finds recipe by id, if it exists.
     * @param id the id of the recipe to be found
     * @return the found recipe
     * @throws NoSuchElementException when recipe not found
     */
    public Recipe getRecipeById(Long id) {
        try {
            return recipeRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new RuntimeException("Recipe with id " + id + " not found.");
        }
    }

    /**
     * Reads all recipes.
     * @return list of all recipes in database
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
}
