package client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import commons.Ingredient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Shoppinglist class that will be serialized to disk to save.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingList implements Serializable {

    @JsonProperty("shoppingItems")
    private List<ShoppingItem> shoppingItems;

    /**
     * Default constructor
     */
    public ShoppingList() {
        this.shoppingItems = new ArrayList<>();
    }

    /**
     * Constructor with initial items
     * @param shoppingItems The initial list of shopping items
     */
    public ShoppingList(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = new ArrayList<>(
            shoppingItems != null ? shoppingItems : Collections.emptyList()
        );
    }

    /**
     * Get the shopping items
     * @return list of shopping items
     */
    public List<ShoppingItem> getShoppingItems() {
        return Collections.unmodifiableList(shoppingItems);
    }

    /**
     * Set the shopping items
     * @param shoppingItems list of shopping items
     */
    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        this.shoppingItems = new ArrayList<>(
            shoppingItems != null ? shoppingItems : Collections.emptyList()
        );
    }

    /**
     * Add a shopping item to the list
     * @param item The shopping item
     */
    public void addItem(ShoppingItem item) {
        if (item != null && item.getIngredient() != null) {
            shoppingItems.add(item);
        }
    }

    /**
     * Add a manually added item to the list
     * @param ingredient The ingredient to add
     */
    public void addManualItem(Ingredient ingredient) {
        if (ingredient != null) {
            shoppingItems.add(new ShoppingItem(ingredient, null));
        }
    }

    /**
     * Add a recipe item to the list
     * @param ingredient The ingredient to add
     * @param sourceRecipeName The name of the recipe this item came from
     */
    public void addRecipeItem(Ingredient ingredient, String sourceRecipeName) {
        if (ingredient != null) {
            shoppingItems.add(new ShoppingItem(ingredient, sourceRecipeName));
        }
    }

    /**
     * Remove a shopping item from the list
     * @param index The index of the item to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean removeItem(int index) {
        if (index >= 0 && index < shoppingItems.size()) {
            shoppingItems.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Remove a shopping item from the list by ingredient name
     * @param ingredientName The name of the ingredient to remove
     * @return true if the item was removed, false otherwise
     */
    public boolean removeItem(String ingredientName) {
        if (ingredientName == null) {
            return false;
        }

        for (int i = 0; i < shoppingItems.size(); i++) {
            ShoppingItem item = shoppingItems.get(i);
            if (
                item.getIngredient() != null &&
                ingredientName.equals(item.getIngredient().getName())
            ) {
                shoppingItems.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Clear all items from the list
     */
    public void clearList() {
        shoppingItems.clear();
    }

    /**
     * Check if the list is empty
     * @return true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return shoppingItems.isEmpty();
    }

    /**
     * Get the number of items in the list
     * @return The number of items
     */
    public int size() {
        return shoppingItems.size();
    }
}
