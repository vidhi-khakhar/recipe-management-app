package client.scenes;

import com.google.inject.Inject;

import client.utils.RecipeServerUtils;
import commons.Recipe;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AddButtonCtrl {
    private final MainCtrl mainCtrl;
    private final RecipeServerUtils recipeServerUtils;

    @FXML
    private Button recipeAddButton;

    @FXML
    private TextField recipeInput;

    @FXML
    private Label statusLabel;

    @FXML
    private TextField servingsInput;

    @FXML
    private Button setServingsButton;

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

        if (query.isEmpty()) {
            statusLabel.setText("Please enter a name.");
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            return;
        }

        if (!recipeValidation(query)) {
            statusLabel.setVisible(true);
            return;
        }

        query = query.substring(0, 1).toUpperCase() + query.substring(1);
        Recipe recipe = new Recipe(query);

        int servings = validateServings(servingsInput.getText());
        if (servings == -1) {
            return;
        } else {
            recipe.setServings(servings);
            servingsInput.clear();
        }

        try {
            recipe.setRecipeId(recipeServerUtils.createRecipe(recipe));
            mainCtrl.addRecipe(recipe);

            recipeInput.clear();

            statusLabel.setText("Recipe added!");
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setVisible(true);

            // DOnly close window after 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                Stage stage = (Stage) recipeAddButton.getScene().getWindow();
                stage.close();
            });
            delay.play();

        } catch (Exception e) {
            statusLabel.setText("Something went wrong during adding recipe.");
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            System.out.println(
                "Something went wrong during the creation of a recipe: " + e.getMessage()
            );
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

    /**
     * Validates servings input.
     * @param text the user input to be converted
     * @return -1 if invalid, number of servings otherwise
     */
    @FXML
    private int validateServings(String text) {
        try {
            int servings = Integer.parseInt(text);

            if (servings < 1) {
                showError("More Than 0", "Servings must be an integer more than 0.");
                return -1;
            }

            return servings;

        } catch (NumberFormatException e) {
            showError("Must Be Number", "Servings must be a number.");
            return -1;
        }
    }


    /**
     * A method that creates an error alert with a heading and content
     * @param header The header of the error
     * @param content the content of the error
     */
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

