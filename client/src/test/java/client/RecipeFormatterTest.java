package client;

import commons.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeFormatterTest {
    private RecipeFormatter recipeFormatter;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipeFormatter = new RecipeFormatter();
        recipe = new Recipe("Sarmale", "ro");
        recipe.setIngredients(List.of(
                "varza",
                "carne",
                "orez"));
        recipe.setInstructions(List.of(
                "Sa se toace carnea",
                "Se ruleaza in foi de varza"));
    }

    @Test
    void formatPlaintextTest() {
        String text = recipeFormatter.formatPlaintext(recipe);
        assertTrue(text.contains("Recipe: Sarmale (ro)"));
        assertTrue(text.contains("Ingredients: \n\t- varza"));
        assertTrue(text.contains("Instructions: \n\t1. Sa se toace carnea"));
    }

    @Test
    void formatMarkdownTest() {
        String text = recipeFormatter.formatMarkdown(recipe);
        assertTrue(text.contains("# Recipe: Sarmale (ro)"));
        assertTrue(text.contains("## Ingredients: \n- varza"));
        assertTrue(text.contains("## Instructions: \n1. Sa se toace carnea"));
    }
}