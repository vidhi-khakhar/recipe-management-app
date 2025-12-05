package server.service;

import commons.Ingredient;
import commons.Recipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.IngredientRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


@Service
public class IngredientService {


    @Autowired
    private IngredientRepository ingredientRepository;

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
     * updates the ingredient and recalculates kcal per 100g
     *
     * @param name
     * @param proteinPer100g
     * @param fatPer100g
     * @param carbsPer100g
     * @return an Ingredient with an updated kcalPer100g
     */
    public Ingredient updateIngredientKcal(Long id, String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        double newKcalPer100g = 4 * proteinPer100g + 4 * carbsPer100g + 9 * fatPer100g;
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new NoSuchElementException ("Ingredient not found"));
        ingredient.setKcalPer100g(newKcalPer100g);
        return ingredientRepository.save(ingredient);
    }

    /**
     * creates an ingredient in the repo
     * @param ingredient
     */
    public void createIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
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
//        ingredientToUpdate.setKcalPer100g(updateIngredientKcal(id, updatedIngredient.getName(),
//                updatedIngredient.getProteinPer100g(), updatedIngredient.getFatPer100g(), updatedIngredient.getCarbsPer100g()));

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