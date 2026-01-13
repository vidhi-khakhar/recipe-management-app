package server.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import commons.RecipeIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import commons.Recipe;
import server.database.RecipeRepository;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    
    /**
     * Creates a new RecipeService
     * with required RecipeRepository (constructor DI).
     * @param recipeRepository the repository used to perform CRUD operations on recipes
     * @Autowired tells Spring to automatically inject dependency.
     */
    @Autowired
    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Creates a new recipe.
     * @param recipe recipe to be added to database
     */
    public void createRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public void cloneRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
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
            throw new NoSuchElementException("Recipe with id " + id + " not found.");
        }
    }

    /**
     * Reads all recipes.
     * @return list of all recipes in database
     */
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * Updates a recipe.
     * @param id the id of the recipe to be updated
     * @param updatedRecipe the updated version of the recipe
     */
    public void updateRecipe(Long id, Recipe updatedRecipe) {
        Objects.requireNonNull(updatedRecipe, "Updated recipe cannot be null.");

        Recipe recipeToUpdate = getRecipeById(id);

        recipeToUpdate.setTitle(updatedRecipe.getTitle());
        recipeToUpdate.setServings(updatedRecipe.getServings());
        recipeToUpdate.setLanguage(updatedRecipe.getLanguage());
        recipeToUpdate.setInstructions(updatedRecipe.getInstructions());

        recipeToUpdate.getIngredients().clear();
        if(updatedRecipe.getIngredients() != null) {
            for(RecipeIngredient ingredient : updatedRecipe.getIngredients()) {
                ingredient.setRecipe(recipeToUpdate);
                recipeToUpdate.getIngredients().add(ingredient);
            }
        }

        recipeRepository.save(recipeToUpdate);
    }

    /**
     * Deletes recipe by id.
     * @param id the id of the recipe to be deleted
     * @throws NoSuchElementException when recipe not found
     */
    public void deleteRecipeById(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Recipe with id " + id + " not found.");
        }
    }
}
