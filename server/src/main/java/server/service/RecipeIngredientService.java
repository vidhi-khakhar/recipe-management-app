package server.service;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import server.database.RecipeRepository;
import server.database.IngredientRepository;
import server.database.RecipeIngredientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class RecipeIngredientService {
    private final RecipeIngredientRepository recipeIngredientRepo;
    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;

    /**
     * Creates the service for the RecipeIngredient.
     * @param recipeIngredientRepo the database for recipeIngredients
     * @param recipeRepo the database for recipes
     * @param ingredientRepo the database for ingredients
     */
    public RecipeIngredientService(
            RecipeIngredientRepository recipeIngredientRepo,
            RecipeRepository recipeRepo,
            IngredientRepository ingredientRepo) {
        this.recipeIngredientRepo = recipeIngredientRepo;
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
    }

    /**
     * Creates a recipIngredient entity in the database.
     * @param recipeIngredient the entity to be created
     * @return the created entity
     */
    @Transactional
    public RecipeIngredient createRecipeIngredient(RecipeIngredient recipeIngredient) {
        Ingredient ingredientFromDb = ingredientRepo.findById(recipeIngredient.getId().getIngredientId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        Recipe recipeFromDb = recipeRepo.findById(recipeIngredient.getId().getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipeIngredient.setIngredient(ingredientFromDb);
        recipeIngredient.setRecipe(recipeFromDb);

        return recipeIngredientRepo.save(recipeIngredient);
    }

    /**
     * Gets the recipeIngredient by id.
     * @param id the id of the entity to be found
     * @return the recipeIngredient, if found
     */
    public RecipeIngredient getRecipeIngredient(RecipeIngredientKey id) {
        try {
            return recipeIngredientRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Ingredient with id " +
                    id.getIngredientId() + " does not belong to recipe with id " +
                    id.getRecipeId() + " !");
        }
    }

    /**
     * Gets all recipeIngredients from the database.
     * @return a list of all recipeIngredients
     */
    public List<RecipeIngredient> getAllRecipeIngredients() {
        return recipeIngredientRepo.findAll();
    }

    /**
     * Gets all recipeIngredients for a specific recipe.
     * @param recipeId the id of the recipe
     * @return the list of recipeIngredients for the given recipe
     */
    public List<RecipeIngredient> getRecipeIngredients(Long recipeId) {
        return getAllRecipeIngredients().stream()
                .filter(ri -> ri.getId().getRecipeId().equals(recipeId))
                .toList();
    }

    /**
     * Updates the recipeIngredient.
     * @param id the id of the recipeIngredient to be updated
     * @param updated the updated entity
     */
    public void updateRecipeIngredient(RecipeIngredientKey id, RecipeIngredient updated) {
        Objects.requireNonNull(updated, "Updated recipeIngredient cannot be null!");

        RecipeIngredient toUpdate = getRecipeIngredient(id);
        if (updated.isFormal()) {
            toUpdate.setFormal(updated.getAmount(), updated.getUnit());
        } else {
            toUpdate.setInformal(updated.getInformal());
        }


        recipeIngredientRepo.save(toUpdate);
    }

    /**
     * Deletes a recipeIngredient from the database.
     * @param id the id of the recipeIngredient to be removed
     */
    @Transactional
    public void deleteRecipeIngredient(RecipeIngredientKey id) {
        Recipe recipe = recipeRepo.findById(id.getRecipeId())
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        boolean removed = recipe.getIngredients().removeIf(ri -> ri.getId()
                .equals(id));
        if (!removed) {
            throw new NoSuchElementException("Ingredient not found");
        }

        recipeRepo.save(recipe);
    }
}
