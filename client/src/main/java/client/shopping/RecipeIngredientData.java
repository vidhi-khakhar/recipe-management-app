package client.shopping;

/**
 * Data class representing an ingredient from a recipe in a format
 * suitable for display and editing in the recipe shopping list UI.
 * This class bridges the gap between RecipeIngredient entities and
 * string-based ingredients with a unified interface.
 */
public class RecipeIngredientData {

    private String name;
    private double amount;
    private String unit;
    private boolean formal; // true if has formal amount/unit, false if informal
    private String informal; // informal description like "to taste"
    private boolean customAdded; // true if user manually added this ingredient

    /**
     * Default constructor.
     */
    public RecipeIngredientData() {
        this.name = "";
        this.amount = 0.0;
        this.unit = "";
        this.formal = false;
        this.informal = null;
        this.customAdded = false;
    }

    /**
     * Constructor for informal ingredients.
     * 
     * @param name the ingredient name/description
     * @param informal informal amount description
     */
    public RecipeIngredientData(String name, String informal) {
        this.name = name;
        this.amount = 0.0;
        this.unit = "";
        this.formal = false;
        this.informal = informal;
        this.customAdded = false;
    }

    /**
     * Constructor for formal ingredients.
     * 
     * @param name the ingredient name
     * @param amount the amount
     * @param unit the unit
     */
    public RecipeIngredientData(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.formal = true;
        this.informal = null;
        this.customAdded = false;
    }

    /**
     * Creates a copy of this RecipeIngredientData.
     * 
     * @return a new instance with the same values
     */
    public RecipeIngredientData copy() {
        RecipeIngredientData copy = new RecipeIngredientData();
        copy.name = this.name;
        copy.amount = this.amount;
        copy.unit = this.unit;
        copy.formal = this.formal;
        copy.informal = this.informal;
        copy.customAdded = this.customAdded;
        return copy;
    }

    /**
     * Gets the ingredient name.
     * 
     * @return the ingredient name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the ingredient name.
     * 
     * @param name the ingredient name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ingredient amount.
     * 
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the ingredient amount.
     * 
     * @param amount the amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the ingredient unit.
     * 
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the ingredient unit.
     * 
     * @param unit the unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * Checks if this ingredient has formal amount/unit.
     * 
     * @return true if formal, false if informal
     */
    public boolean isFormal() {
        return formal;
    }

    /**
     * Sets whether this ingredient is formal or informal.
     * 
     * @param formal true for formal, false for informal
     */
    public void setFormal(boolean formal) {
        this.formal = formal;
    }

    /**
     * Gets the informal description.
     * 
     * @return the informal description, or null if formal
     */
    public String getInformal() {
        return informal;
    }

    /**
     * Sets the informal description.
     * 
     * @param informal the informal description
     */
    public void setInformal(String informal) {
        this.informal = informal;
    }

    /**
     * Checks if this ingredient was manually added by the user.
     * 
     * @return true if custom added, false if from recipe
     */
    public boolean isCustomAdded() {
        return customAdded;
    }

    /**
     * Sets whether this ingredient was manually added by the user.
     * 
     * @param customAdded true if custom added, false if from recipe
     */
    public void setCustomAdded(boolean customAdded) {
        this.customAdded = customAdded;
    }

    /**
     * Gets the display text for this ingredient.
     * 
     * @return formatted string for display
     */
    public String getDisplayText() {
        if (formal) {
            return String.format("%s (%.1f %s)", name, amount, unit);
        } else {
            return informal != null ? informal : name;
        }
    }

    /**
     * Converts this ingredient to a ShoppingItem.
     * 
     * @param recipeName the source recipe name
     * @return a ShoppingItem representation
     */
    public client.model.ShoppingItem toShoppingItem(String recipeName) {
        client.model.ShoppingItem item = new client.model.ShoppingItem();
        item.setName(name);
        item.setAmount(amount);
        item.setUnit(unit);
        item.setSourceRecipeName(recipeName);
        return item;
    }

    /**
     * String representation of this ingredient.
     * 
     * @return formatted string representation
     */
    @Override
    public String toString() {
        if (formal) {
            return String.format("%s: %.1f %s", name, amount, unit);
        } else {
            return String.format("%s: %s", name, informal);
        }
    }

    /**
     * Compares this ingredient to another object for equality.
     * 
     * @param obj the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RecipeIngredientData that = (RecipeIngredientData) obj;
        return java.util.Objects.equals(name, that.name) &&
               java.util.Objects.equals(unit, that.unit) &&
               formal == that.formal &&
               java.util.Objects.equals(informal, that.informal);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, unit, formal, informal);
    }
}