package client.scenes;

import java.util.ResourceBundle;

import client.config.ConfigService;
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
    private final ConfigService configService;

    @FXML 
    private Label editRecipeWindowTitle;

    @FXML
    private TextField editRecipeTitle;

    @FXML
    private TextField servingsInput;

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private Button editTitleAddButton;
    
    @FXML
    private Label statusLabel;

    /**
     * Constructor of the edit title controller class.
     */
    @Inject
    public EditTitleCtrl(MainCtrl mainCtrl, RecipeServerUtils recipeServerUtils, ConfigService configService) {
        this.mainCtrl = mainCtrl;
        this.recipeServerUtils = recipeServerUtils;
        this.configService = configService;
    }

    @FXML
    public void initialize() {
        loadLanguages();
    }

    
    /**
     * Translates this ui
     */
    @FXML
    public void loadLanguages() {
        ResourceBundle props = getTranslationsBundle();

        languageChoiceBox.getItems().clear();

        languageChoiceBox.getItems().addAll(
            props.getString("label.english"),
            props.getString("label.dutch"),
            props.getString("label.romanian"),
            props.getString("label.spanish")
        );

        languageChoiceBox.getSelectionModel().selectFirst();
        editRecipeWindowTitle.setText(props.getString("label.editRecipeDetails"));
        editRecipeTitle.setPromptText(props.getString("label.addRecipeName"));
        servingsInput.setPromptText(props.getString("label.addRecipeServings"));
        editTitleAddButton.setText(props.getString("label.editButton"));
    }

    /**
     * Edits the title of recipe.
     */
    @FXML
    private void editTitle() {
        ResourceBundle props = getTranslationsBundle();
        String query = editRecipeTitle.getText().trim();
        if (query.isEmpty()) {
            statusLabel.setText(props.getString("label.emptyRecipeName"));
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            if(recipeValidation(query)){
                query = query.substring(0, 1).toUpperCase() + query.substring(1);
                Recipe recipe = mainCtrl.getSelectedRecipe();

                try{
                    recipe.setTitle(query);

                    String selectedLanguage = languageChoiceBox.getValue().toLowerCase().trim();
                    switch (selectedLanguage) {
                        case "english" -> recipe.setLanguage("En");
                        case "dutch" -> recipe.setLanguage("NL");
                        case "romanian" -> recipe.setLanguage("Ro");
                        case "spanish" -> recipe.setLanguage("Es");
                    }

                    int servings = validateServings(servingsInput.getText());
                    if (servings == -1) {
                        return;
                    } else {
                        recipe.setServings(servings);
                        servingsInput.clear();
                    }

                    recipeServerUtils.replaceRecipe(recipe.getRecipeId(), recipe);

                    editRecipeTitle.clear();
                    statusLabel.setText(props.getString("label.titleHasBeenAdded"));

                    mainCtrl.refreshSidebarList();
                    mainCtrl.updateTitleLabel(query);
                    mainCtrl.updateRecipeLanguageLabel(recipe.getLanguage());

                    statusLabel.setStyle("-fx-text-fill: green;");

                } catch (Exception e) {
                    statusLabel.setText(props.getString("label.titleError"));
                    statusLabel.setStyle("-fx-text-fill: red;");
                    mainCtrl.recoverFromError(props.getString("label.titleError"));
                }
            }
        }


    }

    /**
     * Checks whether the given input suffices the criteria
     * Ensures first letter is capitalized
     * @param title the given recipe name by the user
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
     * A method that creates an error alert with a heading and content
     * @param header The header of the error
     * @param content the content of the error
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

}
