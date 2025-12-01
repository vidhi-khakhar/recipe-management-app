package server.service;

import org.springframework.stereotype.Service;
import server.RecipeRepository;

@Service
public class RecipeIngredientService {
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeIngredientService(
            RecipeRepository recipeRepository,
            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    // TODO: Add ingredient to recipe

    // TODO: Change ingredient in recipe

    // TODO: Delete ingredient in recipe
}
