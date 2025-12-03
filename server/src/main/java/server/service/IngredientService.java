package server.service;

import commons.Ingredient;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.IngredientRepository;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    public Ingredient updateIngredientKcal(Long id, String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        double newKcalPer100g = 4 * proteinPer100g + 4 * carbsPer100g + 9 * fatPer100g;
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Ingredient not found"));
        ingredient.setKcalPer100g(newKcalPer100g);
        return ingredientRepository.save(ingredient);
    }


}
