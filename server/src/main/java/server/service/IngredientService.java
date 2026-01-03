package server.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import commons.Ingredient;
import server.RecipeRepository;
import server.database.IngredientRepository;


@Service
public class IngredientService {


    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * gets the ingredient by it's id
     * @param id
     * @return an Ingredient
     */
    public Ingredient getIngredientById(Long id) {
        try {
            return ingredientRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Ingredient with id " + id + " not found.");
        }
    }

    /**
     * Reads all ingredients.
     * @return list of all ingredients in database
     */
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    /**
     * creates an ingredient in the repo
     * @param ingredient
     * @return Ingredient
     */
    public Ingredient createIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    /**
     * updates the ingredient for when something is changed
     * @param id
     * @param updatedIngredient
     */
    public void updateIngredient(Long id, Ingredient updatedIngredient) {
        Objects.requireNonNull(updatedIngredient, "Updated ingredient cannot be null.");

        Ingredient ingredientToUpdate = getIngredientById(id);

        ingredientToUpdate.setName(updatedIngredient.getName());
        ingredientToUpdate.setProteinPer100g(updatedIngredient.getProteinPer100g());
        ingredientToUpdate.setFatPer100g(updatedIngredient.getFatPer100g());
        ingredientToUpdate.setCarbsPer100g(updatedIngredient.getCarbsPer100g());
        double newKcal = Ingredient.calculatingKcal(updatedIngredient.getProteinPer100g(),
                updatedIngredient.getFatPer100g(), updatedIngredient.getCarbsPer100g());
        ingredientToUpdate.setKcalPer100g(newKcal);

        ingredientRepository.save(ingredientToUpdate);
    }

    /**
     * deletes an ingredient
     * @param id
     */
    public void deleteIngredientById(Long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Ingredient with id " + id + " not found.");
        }
    }


}