package client.scenes;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.utils.RecipeServerUtils;
import commons.Recipe;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AddButtonCtrl {

    private final MainCtrl mainCtrl;
    private final RecipeServerUtils recipeServerUtils;
    private ConfigService configService;

    @FXML 
    private Label addRecipeInfo;

    @FXML
    private Button recipeAddButton;
    @FXML
    private TextField recipeInput;
    @FXML
    private Label statusLabel;

    @FXML
    private TextField servingsInput;
  
    @FXML
    private ChoiceBox<String> languageChoiceBox;

    /**
     * * Constructs an AddButtonCtrl and injects the necessary dependencies. *
     * The injected dependencies allow this controller to communicate with the *
     * server for recipe operations and to update the main UI. * @param mainCtrl
     * the main controller, used to update the UI when a recipe is added
     *
     *
     * @param recipeServerUtils the utility class responsible for server
     * communication, * used to create and manage recipes
     */
    @Inject
    public AddButtonCtrl(MainCtrl mainCtrl, RecipeServerUtils recipeServerUtils, ConfigService configService) {
        this.mainCtrl = mainCtrl;
        this.recipeServerUtils = recipeServerUtils;
        this.configService = configService;
    }

    /**
     * Starts up this ui
     */
    @FXML
    public void initialize() {
        loadLanguages();
    }

    private void loadLanguages() {
        ResourceBundle props = getTranslationsBundle();

        languageChoiceBox.getItems().clear();

        languageChoiceBox.getItems().addAll(
            props.getString("label.english"),
            props.getString("label.dutch"),
            props.getString("label.romanian"),
            props.getString("label.spanish")
        );

        languageChoiceBox.getSelectionModel().selectFirst();

        addRecipeInfo.setText(props.getString("label.addRecipeInfo"));
        recipeInput.setPromptText(props.getString("label.addRecipeName"));
        servingsInput.setPromptText(props.getString("label.addRecipeServings"));
        recipeAddButton.setText(props.getString("label.addRecipeButton"));
    }


    /**
     * * Handles the action of adding a new recipe when the user clicks the
     * "Add" button.
     */
    @FXML
    private void recipeAddRequest() {
        ResourceBundle props = getTranslationsBundle();
        String query = recipeInput.getText().trim();

        // Check if recipe name is empty
        if (query.isEmpty()) {
            statusLabel.setText(props.getString("label.emptyRecipeName"));
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            return;
        }

        // Validate recipe name
        if (!recipeValidation(query)) {
            statusLabel.setVisible(true);
            return;
        }

        // Capitalize first letter
        query = query.substring(0, 1).toUpperCase() + query.substring(1);
        Recipe recipe = new Recipe(query);

        // Validate servings
        int servings = validateServings(servingsInput.getText());
        if (servings == -1) return;
        recipe.setServings(servings);
        servingsInput.clear();

        // Set language based on choice box
        String selectedLanguage = languageChoiceBox.getValue().toLowerCase().trim();
        switch (selectedLanguage) {
            case "english" -> recipe.setLanguage("En");
            case "dutch" -> recipe.setLanguage("NL");
            case "romanian" -> recipe.setLanguage("Ro");
            case "spanish" -> recipe.setLanguage("Es");
        }

        // Try to create recipe and update UI
        try {
            recipe.setRecipeId(recipeServerUtils.createRecipe(recipe));
            mainCtrl.addRecipe(recipe);
            recipeInput.clear();

            statusLabel.setText(props.getString("label.successMessage"));
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setVisible(true);

            // Auto-close after 2 seconds
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> {
                Stage stage = (Stage) recipeAddButton.getScene().getWindow();
                stage.close();
            });
            delay.play();

        } catch (Exception e) {
            statusLabel.setText(props.getString("label.failedrecipeAdd"));
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setVisible(true);
            System.out.println("Something went wrong during the creation of a recipe: " + e.getMessage());
        }
    }


    /**
     * * Checks whether the given input suffices the criteria * Also makes sure
     * the first letter is a capital * @param query the given recipe name by the
     * user
     */
    private boolean recipeValidation(String query) {
        ResourceBundle props = getTranslationsBundle();

        if (query.length() > 20) {
            statusLabel.setText(props.getString("label.tooLongName"));
            statusLabel.setStyle("-fx-text-fill: red;");
            return false;
        }
        if (query.matches(".*[\\*\"'\\@].*")) {
            statusLabel.setText(props.getString("label.illegalName"));
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
        ResourceBundle props = getTranslationsBundle();

        try {
            int servings = Integer.parseInt(text);
            if (servings < 1) {
                showError(props.getString("label.negativeServings"),
                        props.getString("label.negativeServings.body"));
                return -1;
            }
            return servings;
        } catch (NumberFormatException e) {
            showError(props.getString("label.illegalServing"),
                    props.getString("label.illegalServing.body"));
            return -1;
        }
    }


    /**
     * * A method that creates an error alert with a heading and content
     *
     *
     * @param header The header of the error * @param content the content of the
     * error
     */
    private void showError(String header, String content) {
        ResourceBundle props = getTranslationsBundle();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(props.getString("alert.error"));
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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

    public void onShow() {
        loadLanguages();
    }

    
}
