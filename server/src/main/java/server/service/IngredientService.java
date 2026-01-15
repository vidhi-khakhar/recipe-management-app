package server.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import commons.Ingredient;
import server.database.RecipeIngredientRepository;
import server.database.RecipeRepository;
import server.database.IngredientRepository;


@Service
public class IngredientService {
    @Autowired
    private EntityManager entityManager;


    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    /**
     * gets the ingredient by its id
     * @param id the id of the ingredient
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
     * deletes
     * @param ingredientId id
     */
    @Transactional
    public void deleteIngredientById(Long ingredientId) {
        recipeIngredientRepository.deleteByIngredientId(ingredientId);

        ingredientRepository.deleteById(ingredientId);
    }
}