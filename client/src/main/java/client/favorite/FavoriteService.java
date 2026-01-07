package client.favorite;

import java.util.*;
import java.util.stream.Collectors;

import client.config.Config;
import client.config.ConfigService;
import commons.Recipe;

/**
 * Manages the user's favorite recipes locally.
 * Favorites are stored as references (recipe IDs),
 * not as recipe objects.
 */
public class FavoriteService {
    /**
     * Stores IDs of favorite recipes.
     */
    private final Set<Long> favoriteRecipeIds = new HashSet<>();
    private final ConfigService configService;
    private final Config config;

    /**
     * The constructor for the FavoriteService class that links this class to the config
     */
    public FavoriteService() {
        this.configService = ConfigService.getInstance();
        this.config = ConfigService.getConfig();
    }

    /**
     * Fetches the current favorite IDs from the config
     * @param idsFromConfig the current IDs in the config
     */
    public void LoadFavoriteRecipes(Collection<Long> idsFromConfig) {
        favoriteRecipeIds.clear();
        favoriteRecipeIds.addAll(idsFromConfig);
    }

    public Set<Long> getFavoriteRecipeIDs() {
        return Collections.unmodifiableSet(favoriteRecipeIds); // Maybe overkill but why not
    }

    /**
     * Checks whether the given recipe is marked as favorite.
     * @param recipe that is to be checked.
     */
    public boolean isFavorite(Recipe recipe) {
        if (recipe == null || recipe.getRecipeId() == null) {
            return false;
        }
        return favoriteRecipeIds.contains(recipe.getRecipeId());
    }

    /**
     * Marks the given recipe as favorite and saves the change.
     * @param recipe that is to be added.
     */
    public void addFavorite(Recipe recipe) {
        if(recipe == null || recipe.getRecipeId() == null) {
            System.out.println("Recipe is not added to database or recipe does not exist");
            return; // TODO alert user that recipe can't be null
        }

        boolean changed = favoriteRecipeIds.add(recipe.getRecipeId()); // Safety check: checks if correctly added to the list
        if (changed) {
            save();
        }
    }

    /**
     * Removes the given recipe from favorites and saves the change.
     * @param recipe that is to be removed.
     */
    public void removeFavorite(Recipe recipe) {
        if (recipe == null || recipe.getRecipeId() == null) {
            return;
        }
        boolean changed = favoriteRecipeIds.remove(recipe.getRecipeId()); // Safety check: checks if correctly added to the list
        if (changed) {
            save();
        }
    }

    /**
     * Toggles the favorite status of the given recipe.
     * @param recipe that is to be toggled.
     */
    public void toggleFavorite(Recipe recipe) {
        if (isFavorite(recipe)) {
            removeFavorite(recipe);
        } else {
            addFavorite(recipe);
        }
    }

    /**
     * Returns all favorite recipes from the given collection.
     * @param allRecipes collection of all (non-favorite + favorite) live recipes.
     */
    public List<Recipe> getFavoriteRecipes(Collection<Recipe> allRecipes) {
        return allRecipes.stream()
                .filter(this::isFavorite)
                .collect(Collectors.toList());
    }

    /**
     * Removes favorites that no longer exist in the given recipe list.
     * @param allRecipes collection of all (non-favorite + favorite) live recipes.
     * @return IDs of removed favorites for warning purposes.
     */
    public List<Long> removeDeletedFavorites(Collection<Recipe> allRecipes) {
        Set<Long> existingIds = allRecipes.stream()
                .map(Recipe::getRecipeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> deletedIds = favoriteRecipeIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toList());

        if(!deletedIds.isEmpty()) {
            favoriteRecipeIds.removeAll(deletedIds);
            save();
        }

        return deletedIds;
    }

    /**
     * Saves the current favorite IDs to the config file.
     */
    public void save() {
        try{
            config.setFavoriteRecipeIds(new  ArrayList<>(favoriteRecipeIds));
            configService.save(config);
        }
        catch(Exception e){
            System.err.println("Error saving favorite recipes: " + e.getMessage());
        }
    }

}
