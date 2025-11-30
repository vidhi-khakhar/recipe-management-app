package client;

import commons.Recipe;

public class RecipeFormatter {
    public RecipeFormatter() { }

    /**
     * Format recipe as plaintext.
     * @param recipe recipe to be formatted
     * @return recipe formatted as plaintext (String)
     */
    public String formatPlaintext(Recipe recipe) {
        StringBuilder text = new StringBuilder();

        // Recipe: recipe_name (language)
        text.append("Recipe: " + recipe.getTitle());
        text.append(" (" + recipe.getLanguage() + ")\n");

        // Ingredients:
        // - ingredient_1
        // - ...
        text.append("Ingredients: \n");
        for (String ingredient : recipe.getIngredients()) {
            text.append("\t- " + ingredient + "\n");
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
     * @param recipe the recipe to be formatted
     * @return recipe formatted as markdown (String)
     */
    public String formatMarkdown(Recipe recipe) {
        StringBuilder text = new StringBuilder();

        text.append("# Recipe: " + recipe.getTitle());
        text.append(" (" + recipe.getLanguage() + ")\n");

        text.append("## Ingredients: \n");
        for (String ingredient : recipe.getIngredients()) {
            text.append("- " + ingredient + "\n");
        }

        text.append("\n## Instructions: \n");
        int i = 1;
        for (String instruction : recipe.getInstructions()) {
            text.append((i++) + ". " + instruction + "\n");
        }

        return text.toString();
    }
}
