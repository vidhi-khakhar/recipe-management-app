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

    @Inject
    public SearchService(RecipeCollection recipeCollection, FavoriteService favoriteService) {
        this.recipeCollection = recipeCollection;
        this.favoriteService = favoriteService;
    }

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
