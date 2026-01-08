package client.search;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import client.favorite.FavoriteService;
import commons.Recipe;
import com.google.inject.Inject;

public class SearchService {

    private final RecipeCollection recipeCollection;
    private final FavoriteService favoriteService;


    /**
     * Creates a searchService
     * @param recipeCollection the collection of recipes for the search service
     * @param favoriteService the favoriteService for the local machine
     */
    @Inject
    public SearchService(RecipeCollection recipeCollection, FavoriteService favoriteService) {
        this.recipeCollection = recipeCollection;
        this.favoriteService = favoriteService;
    }

    /**
     * Searches for the recipes that comply with the query, also depending on the favoritesOnly parameter
     * @param query the query the user wrote
     * @param favoritesOnly a boolean that states if the user wants to seach in favorites or no
     * @return The list of recipes that correlate with the query
     */
    public List<Recipe> search(String query, boolean favoritesOnly) {

        String[] terms = query.toLowerCase().split("\\s+");

        return recipeCollection.getAll().stream()
                .filter(r -> !favoritesOnly || favoriteService.isFavorite(r))
                .filter(r -> {
                    if(terms.length == 0){
                        return true;
                    }
                    String content = (r.getTitle() + " " +
                            String.join(" ", r.getIngredients()) + " " +
                            String.join(" ", r.getInstructions())).toLowerCase();

                    return Arrays.stream(terms).allMatch(content::contains);
                })
                .collect(Collectors.toList());
    }
}
