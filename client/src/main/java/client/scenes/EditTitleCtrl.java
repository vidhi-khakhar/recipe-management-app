package client.scenes;

import client.utils.RecipeServerUtils;
import commons.Recipe;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Note: This class has borrowed heavily from
 *       AddButtonCtrl, so credit to Simon de Graauw.
 */
public class EditTitleCtrl {
    private final MainCtrl mainCtrl;
    private final RecipeServerUtils recipeServerUtils;

    @FXML
    private TextField servingsInput;

    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private Button editTitleButton;

    @FXML
    private TextField title;

    @FXML
    private Label statusLabel;

    @Inject
    public EditTitleCtrl(MainCtrl mainCtrl, RecipeServerUtils recipeServerUtils) {
        this.mainCtrl = mainCtrl;
        this.recipeServerUtils = recipeServerUtils;
    }

    @FXML
    public void initialize() {
        languageChoiceBox.getItems().addAll("English", "Dutch", "Romanian", "Spanish");
        languageChoiceBox.getSelectionModel().selectFirst();
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

                    String selectedLanguage = languageChoiceBox.getValue().toLowerCase().trim();
                    if (selectedLanguage.equals("english")) {
                        recipe.setLanguage("En");
                    } else if (selectedLanguage.equals("dutch")) {
                        recipe.setLanguage("NL");
                    } else if (selectedLanguage.equals("romanian")) {
                        recipe.setLanguage("Ro");
                    } else if (selectedLanguage.equals("spanish")) {
                        recipe.setLanguage("Es");
                    }

                    int servings = validateServings(servingsInput.getText());
                    if (servings == -1) {
                        return;
                    } else {
                        recipe.setServings(servings);
                        servingsInput.clear();
                    }

                    recipeServerUtils.replaceRecipe(recipe.getRecipeId(), recipe);

                    title.clear();
                    statusLabel.setText("Title has been edited!");


                    mainCtrl.refreshSidebarList();
                    mainCtrl.updateTitleLabel(query);
                    mainCtrl.updateRecipeLanguageLabel(recipe.getLanguage());

                    statusLabel.setStyle("-fx-text-fill: green;");

                } catch (Exception e) {
                    statusLabel.setText("Error editing title of recipe.");
                    statusLabel.setStyle("-fx-text-fill: red;");
                    mainCtrl.recoverFromError("Error editing title of recipe!");
                }
            }
        }


    }

    /**
     * Checks whether the given input suffices the criteria
     * Ensures first letter is capitalized
     * @param title the given recipe name by the user
     */
    private boolean recipeValidation(String title) {
        if (title.length() > 20) {
            statusLabel.setText("The name is longer than 20 characters.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (title.matches(".*[\\*\"'\\`@].*")) {
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
