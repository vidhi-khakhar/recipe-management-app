package client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a single item in the shopping list.
 * This is a client-side snapshot and is NOT linked to server data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingItem implements Serializable {

    private String name; // Name of the item, e.g. "Milk", "Cereal" 
    private double amount; // Amount of the item
    private String unit; // Unit of the amount
    private String sourceRecipeName;  // (null if added directly)
    private boolean bought; // Whether the item has been marked as bought


    /**
     * Default constructor (required for JSON deserialization)
     */
    public ShoppingItem() {}

    /**
     * Constructor for manually added shopping items
     *
     * @param name name of the item
     */
    public ShoppingItem(String name) {
        this.name = name;
        this.amount = 0.0;
        this.unit = ""; // default empty string for unit
        this.sourceRecipeName = null;
    }

    /**
     * Full constructor
     *
     * @param name name of the item
     * @param amount amount
     * @param unit unit (formal or informal)
     * @param sourceRecipeName source recipe name (can be null)
     */
    public ShoppingItem(String name, double amount, String unit, String sourceRecipeName) {
        this.name = name;
        this.amount = amount;
        this.unit = unit != null ? unit : "";
        this.sourceRecipeName = sourceRecipeName;
        this.bought = false;
    }

    /**
     * Get item name
     */
    public String getName() {
        return name;
    }

    /**
     * Set item name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Set unit
     */
    public void setUnit(String unit) {
        this.unit = unit != null ? unit : "";
    }

    /**
     * Get source recipe name
     */
    public String getSourceRecipeName() {
        return sourceRecipeName;
    }

    /**
     * Set source recipe name
     */
    public void setSourceRecipeName(String sourceRecipeName) {
        this.sourceRecipeName = sourceRecipeName;
    }
    
    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public boolean isBought() {
        return bought;
    }   

    /**
     * String representation for UI / printing
     */
    @Override
    public String toString() {
        String source = sourceRecipeName != null ? " (" + sourceRecipeName + ")" : "";
        String unitPart = (unit != null && !unit.isEmpty()) ? unit : "";
        return String.format("%s (%.1f %s)%s", name, amount, unitPart, source);
    }
}
