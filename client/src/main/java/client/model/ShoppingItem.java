package client.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a single item in the shopping list.
 * This is a client-side snapshot and is NOT linked to server data.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingItem implements Serializable {

    /** Name of the item, e.g. "Milk", "Cereal" */
    private String name;

    /** Amount of the item */
    private double amount;

    /** Unit of the amount, can be informal like "box" or formal like "g" */
    private String unit;

    /** Name of the source recipe (null or "Manual" if added directly) */
    private String sourceRecipeName;

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

    /**
     * String representation for UI / printing
     */
    @Override
    public String toString() {
        String source = sourceRecipeName != null ? " (" + sourceRecipeName + ")" : "";
        String unitPart = (unit != null && !unit.isEmpty()) ? unit : "";
        
        if (unitPart.equals("to taste") || unitPart.equals("as needed") 
                || unitPart.equals("to preference") || amount == 0.0) {
            return name + " (" + unitPart + ")" + source;
        }
        
        return String.format("%s (%.1f %s)%s", name, amount, unitPart, source);
    }
}
