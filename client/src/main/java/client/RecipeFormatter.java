package client;

import commons.Recipe;
import commons.RecipeIngredient;

public class RecipeFormatter {
    private final Recipe recipe;

    public RecipeFormatter(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Format recipe as plaintext.
     * @return recipe formatted as plaintext (String)
     */
    public String formatPlaintext() {
        StringBuilder text = new StringBuilder();

        // Recipe: recipe_name (language)
        text.append("Recipe: " + recipe.getTitle());
        text.append(" (" + recipe.getLanguage() + ")\n");
        text.append("Servings: " + recipe.getServings() + "\n");

        // Ingredients:
        // - ingredient_1
        // - ...
        text.append("Ingredients: \n");
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            text.append("\t- " + ingredient.toString() + "\n");
        }

        // Instructions:
        // 1. instruction_1
        // 2. ...
        int i = 1;
        text.append("\nInstructions: \n");
        for (String instruction : recipe.getInstructions()) {
            text.append("\t" + (i++) + ". " + instruction + "\n");
        }

        return text.toString();
    }

    /**
     * Format recipe as markdown.
     * @return recipe formatted as markdown (String)
     */
    public String formatMarkdown() {
        StringBuilder text = new StringBuilder();

        text.append("# Recipe: " + recipe.getTitle());
        text.append(" (" + recipe.getLanguage() + ")\n");

        text.append("## Ingredients: \n");
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            text.append("- " + ingredient.toString() + "\n");
        }

        text.append("\n## Instructions: \n");
        int i = 1;
        for (String instruction : recipe.getInstructions()) {
            text.append((i++) + ". " + instruction + "\n");
        }

        return text.toString();
    }

    /**
     * Method that returns the Full name of the language, instead of the abbriviated one from the recipe entity
     * @param RecipeLanguage: the abriviated language from the recipe entity
     * @return: The full name of the recipe language
     */
    public static String getFullRecipeLanguage(String recipeLanguage) {
        String fullRecipeLanguage = recipeLanguage;
        if (recipeLanguage.isEmpty()) {
            return fullRecipeLanguage;
        } if (recipeLanguage.toLowerCase().trim().equals("en")) {
            fullRecipeLanguage = "English";
        } else if (recipeLanguage.toLowerCase().trim().equals("nl")) {
            fullRecipeLanguage = "Nederlands";
        } else if (recipeLanguage.toLowerCase().trim().equals("es")) {
            fullRecipeLanguage = "Españiol";
        } else if (recipeLanguage.toLowerCase().trim().equals("ro")) {
            fullRecipeLanguage = "română";
        }

        return fullRecipeLanguage;
    }

}
