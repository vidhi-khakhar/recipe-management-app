package client.scenes;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.favorite.FavoriteService;
import commons.Recipe;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

/**
 * A custom ListCell for displaying Recipe items in a ListView.
 * Shows the recipe title and a star button to mark or unmark favorites.
 */
public class RecipeListCell extends ListCell<Recipe> {
    private final FavoriteService favoriteService;
    private ConfigService configService;

    private final Label titleLabel = new Label();
    private final Button starButton = new Button();
    private final HBox layout = new HBox(8);

    /**
     * Creates a new RecipeListCell with access to the FavoriteService.
     * Sets up the layout, styling, and favorite toggle behavior.
     *
     * @param favoriteService service used to check and update favorite recipes
     */
    @Inject
    public RecipeListCell(FavoriteService favoriteService, ConfigService configService) {
        this.favoriteService = favoriteService;
        this.configService = configService;

        titleLabel.getStyleClass().add("recipe-title");
        layout.getStyleClass().add("recipe-cell");
        starButton.getStyleClass().add("star-button");

        starButton.setFocusTraversable(false);

        starButton.setOnAction(e -> {
            Recipe recipe = getItem();
            if (recipe != null) {
                if(favoriteService.toggleFavorite(recipe)) {
                    getListView().refresh();
                }else{
                    errorAlert();
                }
            }
            e.consume();
        });

        layout.getChildren().addAll(titleLabel, starButton);
    }

    /**
     * Shows an alert when a recipe cannot be favorited because it is invalid
     * or has not been saved yet.
     */
    private void errorAlert() {
        ResourceBundle props = getTranslationsBundle();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(props.getString("alert.somethingWentWrong"));
        alert.setHeaderText(null);
        alert.setContentText(props.getString("alert.reloadFailed"));
        alert.showAndWait();
    }

    /**
     * Updates the cell's content when the item or its state changes.
     * Displays the recipe title and updates the star button based on
     * whether the recipe is marked as a favorite.
     *
     * @param item  the recipe to display
     * @param empty whether the cell should be cleared
     */
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

    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return java.util.ResourceBundle.getBundle("client.scenes.Translations", new java.util.Locale(languageCode));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load language bundle for code: " + configService.getLanguage());
            return java.util.ResourceBundle.getBundle("client.scenes.Translations", java.util.Locale.ENGLISH);
        }
    }

}
