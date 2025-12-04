package client.scenes;

import client.utils.RecipeServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeCollection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddButtonCtrl {
    private final MainCtrl mainCtrl;
    private final RecipeServerUtils recipeServerUtils;

    @FXML
    private Button recipeAddButton;

    @FXML
    private TextField recipeInput;

    @FXML
    private Label statusLabel;

    /**
     * Constructs an AddButtonCtrl and injects the necessary dependencies.
     * The injected dependencies allow this controller to communicate with the
     * server for recipe operations and to update the main UI.
     * @param mainCtrl the main controller, used to update the UI when a recipe is added
     * @param recipeServerUtils the utility class responsible for server communication,
     *                          used to create and manage recipes
     */
    @Inject
    public AddButtonCtrl(MainCtrl mainCtrl,  RecipeServerUtils recipeServerUtils) {
        this.mainCtrl = mainCtrl;
        this.recipeServerUtils = recipeServerUtils;
    }

    /**
     * Handles the action of adding a new recipe when the user clicks the "Add" button.
     */
    @FXML
    private void recipeAddRequest() {
        String query = recipeInput.getText().trim();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a name."); // Can be removed in the future
            statusLabel.setText("Please enter a name.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            System.out.println("Adding request for: " + query); // Can be removed in the future
            if(recipeValidation(query)){
                query = query.substring(0, 1).toUpperCase() + query.substring(1);
                Recipe recipe = new Recipe(query);

                try{
                    recipe.setRecipeId(recipeServerUtils.createRecipe(recipe));

                    mainCtrl.addRecipe(recipe); // update the UI

                    recipeInput.clear();
                    statusLabel.setText("Recipe added!");
                    statusLabel.setStyle("-fx-text-fill: green;");

                } catch (Exception e) {
                    statusLabel.setText("Something went wrong during adding recipe.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    System.out.println("Something went wrong during the creation of a recipe: " + e.getMessage());
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
}

