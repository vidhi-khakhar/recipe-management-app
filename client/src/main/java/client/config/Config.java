package client.config;

import java.util.List;
import java.util.Locale;

public class Config {

    private String serverUrl;
    private List<Long> favoriteRecipeIds;
    private Locale language;
    private List<Long> shoppingListIds;

    /**
     * Default constructor required for JSON deserialization by Jackson.
     * Creates an empty Config instance that will be populated when loading from file.
     */
    public Config(){}

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

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public List<Long> getShoppingListIds() {
        return shoppingListIds;
    }

    public void setShoppingListIds(List<Long> shoppingListIds) {
        this.shoppingListIds = shoppingListIds;
    }
}
