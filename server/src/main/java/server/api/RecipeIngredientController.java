package server.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.RecipeIngredientService;

@RestController
public class RecipeIngredientController {
    private final RecipeIngredientService recipeIngredientService;

    public RecipeIngredientController(RecipeIngredientService recipeIngredientService) {
        this.recipeIngredientService = recipeIngredientService;
    }

    // TODO: Add ingredient in recipe
    @PostMapping

    // TODO: Change ingredient in recipe
    @PutMapping

    // TODO: Delete ingredient in recipe
    @DeleteMapping
}
