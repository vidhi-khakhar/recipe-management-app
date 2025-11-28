package commons;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an in-memory collection of Recipe objects.
 * The actual persistence of recipes is handled by the Recipe entity
 * and the database/repository layer on the server.
 */

public class RecipeCollection {

    /**
     * Internal storage of recipes, keyed by their database id.
     */
    private final Map<Long, Recipe> recipes = new LinkedHashMap<>();

    /**
     * Creates an empty RecipeCollection.
     */
    public RecipeCollection() {}

    /**
     * Creates a RecipeCollection pre-filled with the given recipes.
     * @param recipes collection of recipes to add
     * @throws IllegalArgumentException if any recipe has a null id
     */
    public RecipeCollection(Collection<Recipe> recipes) {
        recipes.forEach(this::add);
    }

    /**
     * Returns the number of recipes in this collection.
     * @return number of recipes
     */
    public int size() {
        return recipes.size();
    }

    /**
     * Returns whether this collection is empty.
     * @return true if there are no recipes, false otherwise
     */
    public boolean isEmpty() {
        return recipes.isEmpty();
    }

    /**
     * Returns all recipes in this collection as a new List.
     * @return list of all recipes
     */
    public List<Recipe> getAll() {
        return new ArrayList<>(recipes.values());
    }

    /**
     * Retrieves a recipe by its database id.
     * @param id database id of the recipe
     * @return an Optional containing the recipe if present,
     *         or Optional.empty() if not found
     */
    public Optional<Recipe> getById(Long id) {
        return Optional.ofNullable(recipes.get(id));
    }

    /**
     * Adds a recipe to the collection
     * @param recipe the recipe to add
     * @throws IllegalArgumentException if recipe has a null id
     * @throws NullPointerException     if recipe is null
     */
    public void add(Recipe recipe) {
        Long id = recipe.getRecipeId();
        if (id == null) {
            throw new IllegalArgumentException("Recipe must have an id before adding");
        }
        recipes.put(id, recipe);
    }

    /**
     * Removes a recipe with the given id from the collection.
     * @param id the id of the recipe to remove
     */
    public void remove(Long id) {
        recipes.remove(id);
    }

    /**
     * Removes all recipes from this collection.
     */
    public void clear() {
        recipes.clear();
    }

    /**
     * Performs a case-insensitive search by title.
     * @param query the text to search for in recipe titles
     * @return list of matching recipes, sorted alphabetically by title (case-insensitive)
     */
    public List<Recipe> searchByTitle(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return recipes.values().stream()
                .filter(r -> r.getTitle() != null &&
                        r.getTitle().toLowerCase().contains(q))
                .sorted(Comparator.comparing(r -> r.getTitle().toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new RecipeCollection containing only the recipes that
     * satisfy the given predicate.
     * @param p a predicate that returns true for recipes that
     * should be included in the new collection
     * @return a new RecipeCollection containing only the filtered recipes
     * @throws NullPointerException if predicate is null
     */
    public RecipeCollection filter(java.util.function.Predicate<Recipe> p) {
        return new RecipeCollection(
                recipes.values().stream().filter(p).collect(Collectors.toList())
        );
    }


    /**
     * Indicates whether some other object is "equal to" this RecipeCollection.
     * @param o the reference object with which to compare
     * @return whether the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeCollection that = (RecipeCollection) o;
        return Objects.equals(recipes, that.recipes);
    }


    /**
     * Returns a hash code value for this RecipeCollection
     * @return a hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(recipes);
    }
}
