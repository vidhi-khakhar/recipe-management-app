package client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Client-side shopping list.
 * Stored locally (e.g. config.json), never on the server.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoppingList implements Serializable {

    @JsonProperty("shoppingItems")
    private List<ShoppingItem> shoppingItems;

    private transient ObservableList<ShoppingItem> observableItems;

    /**
     * Default constructor
     */
    public ShoppingList() {
        this.shoppingItems = new ArrayList<>();
        this.observableItems = FXCollections.observableArrayList();
    }

    /**
     * Get the observable list for observing changes
     */
    public ObservableList<ShoppingItem> getObservableItems() {
        if (observableItems == null) {
            observableItems = FXCollections.observableArrayList(shoppingItems);
        }
        return observableItems;
    }

    /**
     * Get all shopping items (read-only)
     */
    public List<ShoppingItem> getShoppingItems() {
        return Collections.unmodifiableList(shoppingItems);
    }

    /**
     * Add a shopping item
     */
    public void addItem(ShoppingItem item) {
        if (item != null) {
            shoppingItems.add(item);
            sortAlphabetically();
            if (observableItems != null) {
                observableItems.setAll(shoppingItems);
            }
        }
    }

    /**
     * Remove item by index
     */
    public void removeItemAt(int index) {
        if (index >= 0 && index < shoppingItems.size()) {
            shoppingItems.remove(index);
            if (observableItems != null) {
                observableItems.setAll(shoppingItems);
            }
        }
    }

    /**
     * Remove a specific item instance
     */
    public void removeItem(ShoppingItem item) {
        shoppingItems.remove(item);
        if (observableItems != null) {
            observableItems.setAll(shoppingItems);
        }
    }

    /**
     * Clear the shopping list
     */
    public void clear() {
        shoppingItems.clear();
        if (observableItems != null) {
            observableItems.setAll(shoppingItems);
        }
    }

    /**
     * Check if list is empty
     */
    @JsonIgnore
    public boolean isEmpty() {
        return shoppingItems.isEmpty();
    }

    /**
     * Sort items alphabetically (A–Z)
     */
    public void sortAlphabetically() {
        shoppingItems.sort(
            Comparator.comparing(
                ShoppingItem::getName,
                String.CASE_INSENSITIVE_ORDER
            )
        );
        if (observableItems != null) {
            observableItems.setAll(shoppingItems);
        }
    }

    /**
     * Number of items
     */
    public int size() {
        return shoppingItems.size();
    }
}
