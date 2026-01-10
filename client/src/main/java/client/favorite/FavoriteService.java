package client.favorite;

import java.util.*;
import java.util.stream.Collectors;

import client.config.Config;
import client.config.ConfigService;
import com.google.inject.Inject;
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

    /**
     * The constructor for the FavoriteService class that links this class to the config
     */
    @Inject
    public FavoriteService(ConfigService configService) {
        this.configService = configService;
        loadFromConfig();
    }

    /**
     * Loads favorite IDs from the current config
     */
    public void loadFromConfig() {
        Config config = configService.getConfig();
        if (config != null && config.getFavoriteRecipeIds() != null) {
            favoriteRecipeIds.clear();
            favoriteRecipeIds.addAll(config.getFavoriteRecipeIds());
        }
    }

    /**
     * Displays favorite IDs as an unmodifiable set (read-only)
     * @return the set with favorited IDs
     */
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
     * @return boolean whether action was successful
     */
    public boolean addFavorite(Recipe recipe) {
        if(recipe == null || recipe.getRecipeId() == null) {
            return false; // TODO alert user that recipe can't be null
        }

        // Safety check: checks if correctly added to the list
        boolean changed = favoriteRecipeIds.add(recipe.getRecipeId());
        if (changed) {
            save();
            return true;
        }
        return false;
    }

    /**
     * Removes the given recipe from favorites and saves the change.
     * @param recipe that is to be removed.
     * @return boolean whether action was successful
     */
    public boolean removeFavorite(Recipe recipe) {
        if (recipe == null || recipe.getRecipeId() == null) {
            return false;
        }

        // Safety check: checks if correctly added to the list
        boolean changed = favoriteRecipeIds.remove(recipe.getRecipeId());
        if (changed) {
            save();
            return true;
        }
        return false;
    }

    /**
     * Toggles the favorite status of the given recipe.
     * @param recipe that is to be toggled.
     * @return boolean whether action was successful
     */
    public boolean toggleFavorite(Recipe recipe) {
        if (isFavorite(recipe)) {
            return removeFavorite(recipe);
        } else {
            return addFavorite(recipe);
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
        Config config = configService.getConfig();
        config.setFavoriteRecipeIds(new ArrayList<>(favoriteRecipeIds));
        configService.save();
    }

}
