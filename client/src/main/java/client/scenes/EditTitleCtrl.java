package client.scenes;

import client.utils.RecipeServerUtils;
import commons.Recipe;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class EditTitleCtrl {
    private final MainCtrl mainCtrl;
    private final RecipeServerUtils recipeServerUtils;

    @Inject
    public EditTitleCtrl(MainCtrl mainCtrl, RecipeServerUtils recipeServerUtils) {
        this.mainCtrl = mainCtrl;
        this.recipeServerUtils = recipeServerUtils;
    }

    /**
     * Edits the title of recipe.
     */
    @FXML
    private void editTitle() {
        String query = title.getText().trim();
        if (query.isEmpty()) {
            statusLabel.setText("Please enter a name.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            if(recipeValidation(query)){
                query = query.substring(0, 1).toUpperCase() + query.substring(1);
                Recipe recipe = mainCtrl.getSelectedRecipe();

                try{
                    recipe.setTitle(query);

                    title.clear();
                    statusLabel.setText("Title has been edited!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                } catch (Exception e) {
                    statusLabel.setText("Error editing title of recipe.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                }
            }
        }
    }

    /**
     * Checks whether the given input suffices the criteria
     * Also makes sure the first letter is a capital
     * @param query the given recipe name by the user
     */
    private boolean recipeValidation(String query) {
        if (query.length() > 20) {
            statusLabel.setText("The name is longer than 20 characters.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (query.matches(".*[\\*\"'\\`@].*")) {
            statusLabel.setText("The name contains illegal characters.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        return true;
    }

    @FXML
    private Button editTitleButton;

    @FXML
    private TextField title;

    @FXML
    private Label statusLabel;
}
