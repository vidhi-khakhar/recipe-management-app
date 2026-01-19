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

    /**
     * Getting the colour that the text of the language of the Recipe will be
     * @param recipeLanguage The language of the actual recipe
     * @return the colour of the text
     */
    public static String getLanguageColour(String recipeLanguage) {
        String colour = "-fx-text-fill: white;";

        if (recipeLanguage.isEmpty()) {
            return colour;
        }

        String language = recipeLanguage.toLowerCase().trim();

        if (language.equals("en")) {
            colour = "-fx-text-fill: blue;";
        } else if (language.equals("nl")) {
            colour = "-fx-text-fill: orange;";
        } else if (language.equals("es")) {
            colour = "-fx-text-fill: red;";
        } else if (language.equals("ro")) {
            colour = "-fx-text-fill: yellow;";
        }

        return colour;
    }

    /**
     * Method that gets the FontStyles for all the added languages
     * @param recipeLanguage the language from the recipe
     * @return the font style of the language
     */
    public static String getLanguageFontStyles(String recipeLanguage) {
        String styles = "-fx-text-weight: normal; -fx-font-style: normal; -fx-underline: false";

        if (recipeLanguage.isEmpty()) {
            return styles;
        }

        String language = recipeLanguage.toLowerCase().trim();

        if (language.equals("en")) {
            styles = "-fx-font-weight: bold";
        } else if (language.equals("nl")) {
            styles = "-fx-font-weight: bold; -fx-font-style: italic; -fx-underline: true";
        } else if (language.equals("es")) {
            styles = "-fx-font-style: italic";
        } else if (language.equals("ro")) {
            styles = "-fx-underline: true";
        }

        return styles;
    }
}
