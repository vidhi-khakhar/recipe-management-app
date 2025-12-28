package server.service;

import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.RecipeRepository;
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

    public RecipeIngredientService(
            RecipeIngredientRepository recipeIngredientRepo,
            RecipeRepository recipeRepo,
            IngredientRepository ingredientRepo) {
        this.recipeIngredientRepo = recipeIngredientRepo;
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
    }

    public RecipeIngredient createRecipeIngredient(RecipeIngredient recipeIngredient) {
        Ingredient ingredientFromDb = ingredientRepo.findById(recipeIngredient.getIngredient().getId())
                .orElseThrow(() -> new RuntimeException("Ingredient not found"));
        Recipe recipeFromDb = recipeRepo.findById(recipeIngredient.getRecipe().getRecipeId())
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipeIngredient.setIngredient(ingredientFromDb);
        recipeIngredient.setRecipe(recipeFromDb);

        return recipeIngredientRepo.save(recipeIngredient);
    }

    public RecipeIngredient getRecipeIngredient(RecipeIngredientKey id) {
        try {
            return recipeIngredientRepo.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Ingredient with id " +
                    id.getIngredientId() + " does not belong to recipe with id " +
                    id.getRecipeId() + " !");
        }
    }

    public List<RecipeIngredient> getAllRecipeIngredients() {
        return recipeIngredientRepo.findAll();
    }

    public List<RecipeIngredient> getRecipeIngredients(Long recipeId) {
        return getAllRecipeIngredients().stream()
                .filter(ri -> ri.getId().getRecipeId().equals(recipeId))
                .toList();
    }

    public void updateRecipeIngredient(RecipeIngredientKey id, RecipeIngredient updated) {
        Objects.requireNonNull(updated, "Updated recipeIngredient cannot be null!");

        RecipeIngredient toUpdate = getRecipeIngredient(id);
        if (updated.isFormal()) {
            toUpdate.setFormal(updated.getAmount(), updated.getUnit());
        } else {
            toUpdate.setInformal(updated.getInformal());
        }

        toUpdate.setIngredient(updated.getIngredient());
        toUpdate.setRecipe(updated.getRecipe());

        recipeIngredientRepo.save(toUpdate);
    }

    public void deleteRecipeIngredient(RecipeIngredientKey id) {
        if (recipeIngredientRepo.existsById(id)) {
            recipeIngredientRepo.deleteById(id);
        } else {
            throw new NoSuchElementException("Ingredient with id " +
                    id.getIngredientId() + " does not belong to recipe with id " +
                    id.getRecipeId() + " !");
        }
    }
}
