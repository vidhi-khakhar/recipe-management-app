package client.config;

import java.util.ArrayList;
import java.util.List;

import client.model.ShoppingList;

public class Config {

    private String serverUrl;
    private List<Long> favoriteRecipeIds;
    private String language;
    private ShoppingList shoppingList;
    private Long shoppingListReminder;

    /**
     * Default constructor for Jackson
     */
    public Config() {
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public List<Long> getFavoriteRecipeIds() {
        return favoriteRecipeIds;
    }

    public void setFavoriteRecipeIds(List<Long> favoriteRecipeIds) {
        this.favoriteRecipeIds = favoriteRecipeIds;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    private List<String> filteredLanguages;

    /**
     * This method checks if there is something in the filterlangages
     * @return if not it returns an empty one, otherwise if just returns the normal one
     */
    public List<String> getFilteredLanguages() {
        if (filteredLanguages == null) {
            filteredLanguages = new ArrayList<>();
        }
        return filteredLanguages;
    }

    public void setFilteredLanguages(List<String> filteredLanguages) {
        this.filteredLanguages = filteredLanguages;
    }

    /**
     * Gets the shopping list, initializing it if necessary.
     */
    public ShoppingList getShoppingList() {
        if (shoppingList == null) {
            shoppingList = new ShoppingList();
        }
        return shoppingList;
    }

    public void setShoppingList(ShoppingList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public Long getShoppingListReminder() {
        return shoppingListReminder;
    }

    public void setShoppingListReminder(Long shoppingListReminder) {
        this.shoppingListReminder = shoppingListReminder;
    }
}
