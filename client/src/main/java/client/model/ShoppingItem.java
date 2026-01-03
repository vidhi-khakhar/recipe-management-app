package client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import commons.Ingredient;
import commons.Unit;

/**
 * Represents a single item in the shopping list.
 * Contains the ingredient and source recipe
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingItem implements Serializable {

    private Ingredient ingredient;
    private String sourceRecipeName;
    private double amount;
    private Unit unit;

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
        this.amount = 0.0;
        this.unit = Unit.G; // Default unit
    }

    /**
     * Constructor with ingredient, amount and unit
     * @param ingredient ingredient to set
     * @param amount amount to set
     * @param unit unit to set
     * @param sourceRecipeName name of the recipe this item came from (can be null)
     */
    public ShoppingItem(Ingredient ingredient, double amount, Unit unit, String sourceRecipeName) {
        this.ingredient = ingredient;
        this.amount = amount;
        this.unit = unit;
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
     * Get amount
     * @return amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set amount
     * @param amount amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get unit
     * @return unit
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Set unit
     * @param unit unit to set
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
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

    /**
     * String representation of ShoppingItem
     * @return string representation
     */
    @Override
    public String toString() {
        if (ingredient != null) {
            return String.format("%s (%.1f %s)", ingredient.getName(), amount, unit);
        }
        return "null";
    }
}
