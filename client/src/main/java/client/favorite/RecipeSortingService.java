package client.favorite;

import client.search.RecipeCollection;
import com.google.inject.Inject;
import commons.Recipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The blueprint for creating the meaningful addition to the Search Extension
 */
public class RecipeSortingService {
    private final RecipeCollection recipeCollection;

    /**
     * The constructor for this class. This allows access to the "actual" list
     * of recipes.
     * @param recipeCollection the main source of truth.
     */
    @Inject
    public RecipeSortingService(RecipeCollection recipeCollection) {
        this.recipeCollection = recipeCollection;
    }

    // --- validation ---

    /**
     * Returns whether the collection can be sorted.
     * Blueprint.
     */
    public boolean isSortable(List<Recipe> collection) {
        return collection != null && !collection.isEmpty();
    }

    // --- sorting ---

    /**
     * Sorts recipes alphabetically (A-Z) by title.
     *
     * @param collection the collection to be sorted.
     * @return the sorted collection by title.
     */
    public List<Recipe> sortByTitleAsc(List<Recipe> collection) {
        if (!isSortable(collection)) {
            return List.of();
        }

        List<Recipe> sorted = new ArrayList<>(collection);
        sorted.sort(Comparator.comparing(
                Recipe::getTitle,
                String.CASE_INSENSITIVE_ORDER
        ));

        return List.copyOf(sorted);
    }

    /**
     * Sorts recipes reversed alphabetically (Z-A) by title.
     *
     * @param collection the collection to be sorted.
     * @return the sorted collection by title.
     */
    public List<Recipe> sortByTitleDesc(List<Recipe> collection) {
        if (!isSortable(collection)) {
            return List.of();
        }

        List<Recipe> sorted = new ArrayList<>(collection);
        sorted.sort(Comparator.comparing(
                Recipe::getTitle,
                String.CASE_INSENSITIVE_ORDER
        ).reversed());

        return List.copyOf(sorted);
    }

    /**
     * Sorts the given list by time of creation of the recipes (OLD -> NEW).
     * This is done by using the original list as sorted by date.
     *
     * @return the sorted collection by date.
     */

    public List<Recipe> sortByDateAsc(List<Recipe> recipes) {
        return List.copyOf(recipes);
    }
    /**
     * Sorts the given list by time of creation of the recipes (NEW -> OLD).
     * This is done by using the original list as sorted by date.
     *
     * @return the sorted collection by date.
     */
    public List<Recipe> sortByDateDesc() {
        List<Recipe> sorted = new ArrayList<>(recipeCollection.getAll().reversed());
        return List.copyOf(sorted);
    }

    // --- helpers ---
    /**
     * Returns a preview of sorted recipes.
     * Intended for UI usage such as search results or favorites.
     *
     * @param collection the collection of recipes
     * @param max the maximum number of recipes in the preview
     * @return a limited, sorted list of recipes
     */
    public List<Recipe> getPreview(List<Recipe> collection, int max) {
        if (!isSortable(collection)) {
            return List.of();
        }

        List<Recipe> sorted = sortByTitleAsc(collection);
        return List.copyOf(
                sorted.subList(0, Math.min(sorted.size(), max))
        );
    }

    /**
     * Limits the amount of displayed recipes.
     * @param recipes the recipes.
     * @param max maximum amount of recipes to be displayed.
     * @return the list with the max applied.
     */
    public List<Recipe> limit(List<Recipe> recipes, int max) {
        if (!isSortable(recipes)) {
            return List.of();
        }

        return List.copyOf(
                recipes.subList(0, Math.min(recipes.size(), max))
        );
    }

    /**
     * Returns the first recipe in the list.
     * @param recipes the recipes to be checked
     * @return the first recipe
     */
    public Optional<Recipe> getFirst(List<Recipe> recipes) {
        if (recipes == null || recipes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(recipes.getFirst());
    }
}