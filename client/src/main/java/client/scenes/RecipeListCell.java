package client.scenes;

import client.favorite.FavoriteService;
import com.google.inject.Inject;
import commons.Recipe;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class RecipeListCell extends ListCell<Recipe> {
    private final FavoriteService favoriteService;

    private final Label titleLabel = new Label();
    private final Button starButton = new Button();
    private final HBox layout = new HBox(8);

    @Inject
    public RecipeListCell(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;

        titleLabel.getStyleClass().add("recipe-title");
        layout.getStyleClass().add("recipe-cell");
        starButton.getStyleClass().add("star-button");

        starButton.setFocusTraversable(false);

        starButton.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                favoriteService.toggleFavorite(recipe);
                getListView().refresh();
            }
            e.consume();
        });

        layout.getChildren().addAll(titleLabel, starButton);
    }

    @Override
    protected void updateItem(Recipe item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            setStyle("");
            return;
        }

        titleLabel.setText(item.getTitle());

        boolean isFavorite = favoriteService.isFavorite(item);

        starButton.setText(isFavorite ? "★" : "☆");
        starButton.setDisable(item.getRecipeId() == null);

        setGraphic(layout);
    }

}
