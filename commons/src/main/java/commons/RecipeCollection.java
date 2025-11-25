package commons;

import java.util.*;
import java.util.stream.Collectors;

public class RecipeCollection {

    private final Map<Long, Recipe> recipes = new LinkedHashMap<>();

    public RecipeCollection() {}

    public RecipeCollection(Collection<Recipe> recipes) {
        recipes.forEach(this::add);
    }

    public int size() {
        return recipes.size();
    }

    public List<Recipe> getAll() {
        return new ArrayList<>(recipes.values());
    }

    public Optional<Recipe> getById(Long id) {
        return Optional.ofNullable(recipes.get(id));
    }

    public void add(Recipe recipe) {
        Objects.requireNonNull(recipe);
        Long id = recipe.getRecipeId();
        if (id == null) {
            throw new IllegalArgumentException("Recipe must have an id before adding");
        }
        recipes.put(id, recipe);
    }

    public void remove(Long id) {
        recipes.remove(id);
    }

    public void clear() {
        recipes.clear();
    }

    public List<Recipe> searchByTitle(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase(Locale.ROOT);
        return recipes.values().stream()
                .filter(r -> r.getTitle() != null &&
                        r.getTitle().toLowerCase(Locale.ROOT).contains(q))
                .sorted(Comparator.comparing(r -> r.getTitle().toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }

    public RecipeCollection filter(java.util.function.Predicate<Recipe> p) {
        return new RecipeCollection(
                recipes.values().stream().filter(p).collect(Collectors.toList())
        );
    }


}
