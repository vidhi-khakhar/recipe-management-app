package server.service;

import commons.Ingredient;
import commons.Recipe;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.IngredientRepository;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient getIngredientById(Long id) {
        try {
            return ingredientRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Ingredient with id " + id + " not found.");
        }
    }

    public Ingredient updateIngredientKcal(Long id, String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        double newKcalPer100g = 4 * proteinPer100g + 4 * carbsPer100g + 9 * fatPer100g;
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));
        ingredient.setKcalPer100g(newKcalPer100g);
        return ingredientRepository.save(ingredient);
    }

    public void createIngredient(Ingredient ingredient) {
        ingredientRepository.save(ingredient);
    }

    public void updateIngredient(Long id, Ingredient updatedIngredient) {
        Objects.requireNonNull(updatedIngredient, "Updated ingredient cannot be null.");

        Ingredient ingredientToUpdate = getIngredientById(id);

        ingredientToUpdate.setName(updatedIngredient.getName());
        ingredientToUpdate.setProteinPer100g(updatedIngredient.getProteinPer100g());
        ingredientToUpdate.setFatPer100g(updatedIngredient.getFatPer100g());
        ingredientToUpdate.setCarbsPer100g(updatedIngredient.getCarbsPer100g());

        ingredientRepository.save(ingredientToUpdate);
    }

    public void deleteIngredientById(Long id) {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Ingredient with id " + id + " not found.");
        }
    }
}