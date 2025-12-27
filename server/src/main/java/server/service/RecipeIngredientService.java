package server.service;

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

    public RecipeIngredientService(
            RecipeIngredientRepository recipeIngredientRepo) {
        this.recipeIngredientRepo = recipeIngredientRepo;
    }

    public RecipeIngredient createRecipeIngredient(RecipeIngredient recipeIngredient) {
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
