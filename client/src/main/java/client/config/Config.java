package client.config;

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
