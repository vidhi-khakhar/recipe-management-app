package commons;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an in-memory collection of {Recipe} objects.
 * <p>
 * This class is not a JPA entity and is not persisted directly in the database.
 * Instead, it is a convenience wrapper around a {Map<Long, Recipe>} and
 * is intended to be used both on the server and on the client as:
 * <ul>
 *     <li>a collection containing all recipes,</li>
 *     <li>a filtered subset</li>
 *     <li>a subset such as "favourite recipes".</li>
 * </ul>
 * The actual persistence of recipes is handled by the {Recipe} entity
 * and the database/repository layer on the server.
 */

public class RecipeCollection {

    /**
     * Internal storage of recipes, keyed by their database id.
     * <p>
     * Using a {LinkedHashMap} preserves insertion order, which is often
     * desirable when displaying recipe lists in the UI.
     */
    private final Map<Long, Recipe> recipes = new LinkedHashMap<>();

    /**
     * Creates an empty {RecipeCollection}.
     */
    public RecipeCollection() {}

    /**
     * Creates a {RecipeCollection} pre-filled with the given recipes.
     * Each recipe must have a non-null {recipeId}; otherwise an
     * {IllegalArgumentException} will be thrown.
     *
     * @param recipes collection of recipes to add
     * @throws IllegalArgumentException if any recipe has a null id
     */
    public RecipeCollection(Collection<Recipe> recipes) {
        recipes.forEach(this::add);
    }

    /**
     * Returns the number of recipes in this collection.
     *
     * @return number of recipes
     */
    public int size() {
        return recipes.size();
    }

    /**
     * Returns all recipes in this collection as a new {List}.
     * Modifying the returned list does not affect the internal state of the
     * {RecipeCollection}.
     *
     * @return list of all recipes
     */
    public List<Recipe> getAll() {
        return new ArrayList<>(recipes.values());
    }

    /**
     * Retrieves a recipe by its database id.
     *
     * @param id database id of the recipe
     * @return an {Optional} containing the recipe if present,
     *         or {Optional#empty()} if not found or if {id} is null
     */
    public Optional<Recipe> getById(Long id) {
        return Optional.ofNullable(recipes.get(id));
    }

    /**
     * Adds a recipe to the collection, replacing any existing recipe with the
     * same database id.
     * The recipe must already have a non-null {recipeId} (assigned by the
     * database or server). If the id is null, an {IllegalArgumentException}
     * is thrown.
     *
     * @param recipe the recipe to add (must not be null)
     * @throws IllegalArgumentException if {recipe} has a null id
     * @throws NullPointerException     if {recipe} is null
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
     *
     * @param id the id of the recipe to remove (may be null)
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
     * Performs a simple case-insensitive search by title.
     * If the {query} is {null} or blank, this method returns all
     * recipes. Otherwise, it returns only those recipes whose title contains
     * the query string (case-insensitive).
     *
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
     * Creates a new {RecipeCollection} containing only the recipes that
     * satisfy the given predicate.
     * @param p a predicate that returns {true} for recipes that
     *                  should be included in the new collection
     * @return a new {RecipeCollection} containing only the filtered recipes
     * @throws NullPointerException if {predicate} is null
     */
    public RecipeCollection filter(java.util.function.Predicate<Recipe> p) {
        return new RecipeCollection(
                recipes.values().stream().filter(p).collect(Collectors.toList())
        );
    }


}
