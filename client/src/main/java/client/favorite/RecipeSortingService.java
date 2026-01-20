package client.favorite;

import commons.Recipe;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * The blueprint for creating the meaningful addition to the Search Extension
 */
public class RecipeSortingService {

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
     * Sorts recipes alphabetically by title.
     * Might not yet work completely.
     *
     * @param collection the collection to be sorted.
     * @return the sorted collection by title.
     */
    public List<Recipe> sortByTitle(List<Recipe> collection) {
        System.out.println("Before sort: " + collection);
        collection.sort(Comparator.comparing(Recipe::getTitle));
        System.out.println("After sort: " + collection);
        return collection;
    }

    /**
     * Sorts the given list by date/time of creation of the recipes
     * @param collection the collection to be sorted.
     * @return the sorted collection by date.
     */
    public List<Recipe> sortByDate(List<Recipe> collection) {
        System.out.println("Before sort: " + collection);
        collection.sort(Comparator.comparing(Recipe::getRecipeId).reversed());
        System.out.println("After sort: " + collection);
        return collection;
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
        System.out.println("Creating recipe preview...");

        if (!isSortable(collection)) {
            System.out.println("Collection not sortable");
            return List.of();
        }

        List<Recipe> sorted = sortByTitle(collection);
        List<Recipe> preview = sorted.subList(
                0,
                Math.min(sorted.size(), max)
        );

        System.out.println("Preview result: " + preview);
        return preview;
    }

    /**
     * Limits the amount of displayed recipes.
     * @param recipes the recipes.
     * @param max maximum amount of recipes to be displayed.
     * @return the list with the max applied.
     */
    public List<Recipe> limit(List<Recipe> recipes, int max) {
        System.out.println("Before limit: " + recipes);
        List<Recipe> result = recipes.subList(0, Math.min(recipes.size(), max));
        System.out.println("After limit: " + result);
        return recipes;
    }

    /**
     * Returns the first recipe in the list.
     * @param recipes the recipes to be checked
     * @return the first recipe
     */
    public Optional<Recipe> getFirst(List<Recipe> recipes) {
        System.out.println("Before getFirst: " + recipes);
        List<Recipe> result = recipes.subList(0, Math.min(recipes.size(), recipes.size()));
        System.out.println("After getFirst: " + result);
        return Optional.empty();
    }
}