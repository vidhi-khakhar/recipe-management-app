package client.shopping;

import java.util.ArrayList;
import java.util.List;

import client.model.ShoppingItem;
import client.config.ConfigService;
import commons.Recipe;
import commons.RecipeIngredient;
import client.utils.RecipeServerUtils;
import com.google.inject.Inject;

/**
 * Service for handling recipe shopping list operations.
 * Manages the conversion of recipe ingredients to shopping items
 * and handles serving size scaling.
 */
public class RecipeShoppingService {

    private final RecipeServerUtils recipeServerUtils;
    private final ConfigService configService;

    /**
     * Constructor for dependency injection.
     * @param recipeServerUtils utility for communicating with recipe server
     * @param configService service for accessing configuration
     */
    @Inject
    public RecipeShoppingService(RecipeServerUtils recipeServerUtils, ConfigService configService) {
        this.recipeServerUtils = recipeServerUtils;
        this.configService = configService;
    }

    /**
     * Gets all ingredients for a recipe, combining both formal RecipeIngredient
     * entities and legacy string-based ingredients.
     * Prioritizes string-based ingredients as specified in requirements.
     * 
     * @param recipe the recipe to get ingredients for
     * @return list of ingredient data objects for display and editing
     */
    public List<RecipeIngredientData> getRecipeIngredients(Recipe recipe) {
        List<RecipeIngredientData> ingredients = new ArrayList<>();

        if (recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
            for (RecipeIngredient recipeIngredient : recipe.getIngredients()) {
                ingredients.add(convertFormalIngredient(recipeIngredient, recipe));
            }
        }

        return ingredients;
    }

    /**
     * Converts recipe ingredients to shopping items with serving size scaling applied.
     * 
     * @param ingredients the ingredient data to convert
     * @param originalServings the original serving size of the recipe
     * @param targetServings the desired serving size
     * @param recipeName the name of the source recipe
     * @return list of shopping items ready to be added to the shopping list
     */
    public List<ShoppingItem> convertToShoppingItems(
            List<RecipeIngredientData> ingredients,
            int originalServings,
            int targetServings,
            String recipeName) {
        return convertToShoppingItems(ingredients, originalServings, targetServings, recipeName, false);
    }

    /**
     * Converts recipe ingredients to shopping items with serving size scaling applie
     * 
     * @param ingredients the ingredient data to convert
     * @param originalServings the original serving size of the recipe
     * @param targetServings the desired serving size
     * @param recipeName the name of the source recipe
     * @param alreadyScaled whether the ingredients are already scaled
     * @return list of shopping items ready to be added to the shopping list
     */
    public List<ShoppingItem> convertToShoppingItems(
            List<RecipeIngredientData> ingredients,
            int originalServings,
            int targetServings,
            String recipeName,
            boolean alreadyScaled) {
        
        List<ShoppingItem> shoppingItems = new ArrayList<>();
        double scaleFactor = alreadyScaled ? 1.0 : (double) targetServings / originalServings;

        for (RecipeIngredientData ingredient : ingredients) {
            ShoppingItem item = new ShoppingItem();
            item.setName(ingredient.getName());
            item.setSourceRecipeName(recipeName);

            if (ingredient.isFormal()) {
                // Scale formal ingredients
                double scaledAmount = ingredient.getAmount() * scaleFactor;
                item.setAmount(scaledAmount);
                item.setUnit(ingredient.getUnit());
            } else {
                // Keep informal ingredients as-is
                item.setAmount(ingredient.getAmount());
                item.setUnit(ingredient.getUnit());
            }

            shoppingItems.add(item);
        }

        return shoppingItems;
    }

    /**
     * Applies serving size scaling to a list of ingredients.
     * 
     * @param ingredients the ingredients to scale
     * @param originalServings the original serving size
     * @param targetServings the new serving size
     * @return new list with scaled ingredients
     */
    public List<RecipeIngredientData> scaleIngredients(
            List<RecipeIngredientData> ingredients,
            int originalServings,
            int targetServings) {
        
        List<RecipeIngredientData> scaledIngredients = new ArrayList<>();
        double scaleFactor = (double) targetServings / originalServings;

        for (RecipeIngredientData ingredient : ingredients) {
            RecipeIngredientData scaled = ingredient.copy();
            
            if (scaled.isFormal()) {
                scaled.setAmount(ingredient.getAmount() * scaleFactor);
            }
            // Informal ingredients remain unchanged
            
            scaledIngredients.add(scaled);
        }

        return scaledIngredients;
    }

    /**
     * Adds the converted shopping items to the main shopping list.
     *
     * @param shoppingItems the items to add
     */
    public void addToShoppingList(List<ShoppingItem> shoppingItems) {
        var config = configService.getConfig();
        if (config != null && config.getShoppingList() != null) {
            for (ShoppingItem item : shoppingItems) {
                config.getShoppingList().addItem(item);
            }

            // Save to config.json
            try {
                configService.save();
            } catch (Exception e) {
                System.err.println("Failed to save shopping list to config: " + e.getMessage());
            }
        }
    }

    /**
     * Parses a string-based ingredient into RecipeIngredientData.
     * 
     * @param ingredientStr the ingredient string (e.g., "200g flour", "salt to taste")
     * @param recipe the source recipe
     * @return parsed ingredient data
     */
    private RecipeIngredientData parseStringIngredient(String ingredientStr, Recipe recipe) {
        RecipeIngredientData data = new RecipeIngredientData();
        
        // Try to parse formal amounts (numbers + units)
        String trimmed = ingredientStr.trim();
        
        // Look for patterns like "200g", "2 cups", "1.5 tbsp" etc.
        java.util.regex.Pattern pattern = 
            java.util.regex.Pattern.compile("^([0-9]*\\.?[0-9]+)\\s*([a-zA-Z]+)\\s+(.+)$");
        java.util.regex.Matcher matcher = pattern.matcher(trimmed);
        
        if (matcher.find()) {
            try {
                double amount = Double.parseDouble(matcher.group(1));
                String unit = matcher.group(2);
                String name = matcher.group(3);
                
                data.setName(name.trim());
                data.setAmount(amount);
                data.setUnit(unit);
                data.setFormal(true);
                data.setInformal(null);
            } catch (NumberFormatException e) {
                // If parsing fails, treat as informal
                data.setName(trimmed);
                data.setFormal(false);
                data.setInformal(trimmed);
                data.setAmount(0.0);
                data.setUnit("");
            }
        } else {
            // No formal pattern detected, treat as informal
            data.setName(trimmed);
            data.setFormal(false);
            data.setInformal(trimmed);
            data.setAmount(0.0);
            data.setUnit("");
        }
        
        return data;
    }

    /**
     * Converts a formal RecipeIngredient entity to RecipeIngredientData.
     * 
     * @param recipeIngredient the formal ingredient entity
     * @param recipe the source recipe
     * @return converted ingredient data
     */
    private RecipeIngredientData convertFormalIngredient(
            RecipeIngredient recipeIngredient, Recipe recipe) {
        
        RecipeIngredientData data = new RecipeIngredientData();
        
        if (recipeIngredient.getIngredient() != null) {
            data.setName(recipeIngredient.getIngredient().getName());
        } else {
            data.setName("Unknown Ingredient");
        }
        
        if (recipeIngredient.isFormal()) {
            data.setAmount(recipeIngredient.getAmount() != null ? recipeIngredient.getAmount() : 0.0);
            data.setUnit(recipeIngredient.getUnit() != null ? recipeIngredient.getUnit().toString() : "");
            data.setFormal(true);
            data.setInformal(null);
        } else {
            data.setInformal(recipeIngredient.getInformal());
            data.setFormal(false);
            data.setAmount(0.0);
            data.setUnit("");
        }
        
        return data;
    }
}