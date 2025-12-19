package client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import commons.Ingredient;
import java.io.Serializable;

/**
 * Represents a single item in the shopping list.
 * Contains the ingredient and source recipe
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingItem implements Serializable {

    private Ingredient ingredient;
    private String sourceRecipeName;

    /**
     * Default constructor
     */
    public ShoppingItem() {}

    /**
     * Constructor with ingredient and source recipe name
     * @param ingredient ingredient to set
     * @param sourceRecipeName name of the recipe this item came from (can be null)
     */
    public ShoppingItem(Ingredient ingredient, String sourceRecipeName) {
        this.ingredient = ingredient;
        this.sourceRecipeName = sourceRecipeName;
    }

    /**
     * Get ingredient
     * @return ingredient
     */
    public Ingredient getIngredient() {
        return ingredient;
    }

    /**
     * Set ingredient
     * @param ingredient ingredient to set
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    /**
     * Get source recipe name
     * @return source recipe name (could be null tho)
     */
    public String getSourceRecipeName() {
        return sourceRecipeName;
    }

    /**
     * Set source recipe name
     * @param sourceRecipeName source recipe name to set
     */
    public void setSourceRecipeName(String sourceRecipeName) {
        this.sourceRecipeName = sourceRecipeName;
    }

    @Override
    public String toString() {
        return ingredient != null ? ingredient.toString() : "null";
    }
}
