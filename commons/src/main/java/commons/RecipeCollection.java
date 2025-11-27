package commons;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an in-memory collection of Recipe objects.
 *
 * This class is not a JPA entity and is not persisted directly in the database.
 * Instead, it is a convenience wrapper around a Map<Long, Recipe> and
 * is intended to be used both on the server and on the client as:
 *
 * a collection containing all recipes,
 * a filtered subset
 * a subset such as "favourite recipes".
 *
 * The actual persistence of recipes is handled by the Recipe entity
 * and the database/repository layer on the server.
 */

public class RecipeCollection {

    /**
     * Internal storage of recipes, keyed by their database id.
     * Using a LinkedHashMap preserves insertion order, which is often
     * desirable when displaying recipe lists in the UI.
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
     * Adds a recipe to the collection, replacing any existing recipe with the
     * same database id.
     * @param recipe the recipe to add
     * @throws IllegalArgumentException if recipe has a null id
     * @throws NullPointerException     if recipe is null
     */
    public void add(Recipe recipe) {
        Objects.requireNonNull(recipe);
        Long id = recipe.getRecipeId();
        if (id == null) {
            throw new IllegalArgumentException("Recipe must have an id before adding");
        }
        recipes.put(id, recipe);
    }

    /**
     * Removes a recipe with the given id from the collection.
     * If no recipe with that id exists, nothing happens.
     * @param id the id of the recipe to remove
     */
    public void remove(Long id) {
        recipes.remove(id);
    }

    /**
     * Removes all recipes from this collection.
     * Mainly useful in tests or when resetting state in the client.
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
        String q = query.toLowerCase(Locale.ROOT);
        return recipes.values().stream()
                .filter(r -> r.getTitle() != null &&
                        r.getTitle().toLowerCase(Locale.ROOT).contains(q))
                .sorted(Comparator.comparing(r -> r.getTitle().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new RecipeCollection containing only the recipes that
     * satisfy the given predicate.
     * @param p a predicate that returns true for recipes that
     * should be included in the new collection
     * @return a new {RecipeCollection} containing only the filtered recipes
     * @throws NullPointerException if {predicate} is null
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
