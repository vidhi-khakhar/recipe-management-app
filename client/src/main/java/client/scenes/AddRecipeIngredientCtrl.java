package client.scenes;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.utils.IngredientServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddRecipeIngredientCtrl {

    private Scene nutritionScene;
    private Scene addRecipeIngredientScene;

    private Recipe recipe;
    private RecipeIngredient recipeIngredient;
    private Long ingredientId;
    private boolean isAdd;

    private final IngredientServerUtils ingredientServerUtils;
    private final ConfigService configService;

    @FXML
    private Label addIngredientLabel;

    @FXML
    private TextField amountTextField;

    @FXML
    private Button cancelButton;

    @FXML
    private Label enterAmount;

    @FXML
    private Label infoLabel;

    @FXML
    private ChoiceBox<Ingredient> ingredientChoiceBox;

    @FXML
    private Button newIngredient;

    @FXML
    private Label pleaseAddIngredientLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Label selectIngredient;

    @FXML
    private Label selectUnit;

    @FXML
    private ChoiceBox<Unit> unitChoiceBox;

    @FXML
    private ChoiceBox<String> unitTypeChoiceBox;

    @Inject
    public AddRecipeIngredientCtrl(
            IngredientServerUtils ingredientServerUtils,
            ConfigService configService) {
        this.ingredientServerUtils = ingredientServerUtils;
        this.configService = configService;
    }

    /**
     * Initialize the scenes.
     * @param nutritionScene the nutrition scenes to add a global ingredient
     * @param addRecipeIngredientScene the current add recipe-ingredient scene
     */
    public void initScenes(
        Scene nutritionScene,
        Scene addRecipeIngredientScene
    ) {
        this.nutritionScene = nutritionScene;
        this.addRecipeIngredientScene = addRecipeIngredientScene;
    }

    /**
     * The ChoiceBoxes have the default value of the first element.
     */
    @FXML
    public void onStart(boolean isAdd) {
        this.isAdd = isAdd;
        ResourceBundle props = getTranslationsBundle();
        loadLanguages();

        infoLabel.setVisible(false);
        pleaseAddIngredientLabel.setVisible(false);

        populateIngredientDropdown();

        if (!ingredientServerUtils.getAllIngredients().isEmpty() && isAdd) {
            ingredientChoiceBox.setValue(ingredientChoiceBox.getItems().get(0));
            ingredientChoiceBox.setDisable(false);
            addIngredientLabel.setText(props.getString("label.addIngredientButton"));
        } else if (!ingredientServerUtils.getAllIngredients().isEmpty() && !isAdd) {
            ingredientChoiceBox.setValue(getIngredient(ingredientId));
            ingredientChoiceBox.setDisable(true);
            ingredientChoiceBox.setStyle("-fx-background-color: #E6E6FA");
            addIngredientLabel.setText(props.getString("label.editButton"));

        } else {
            showError(
                props.getString("alert.noIngredients"),
                props.getString("alert.noIngredients.body")
            );
            pleaseAddIngredientLabel.setVisible(true);
        }

        String formalUnit = props.getString("label.formalUnit");
        String informalUnit = props.getString("label.informalUnit");


        unitTypeChoiceBox.getItems().clear();
        unitTypeChoiceBox.getItems().addAll(formalUnit, informalUnit);
        unitTypeChoiceBox.setValue(unitTypeChoiceBox.getItems().get(0));

        unitChoiceBox.getItems().clear();
        unitChoiceBox.setItems(
            FXCollections.observableArrayList(Unit.values())
        );
        unitChoiceBox.setValue(unitChoiceBox.getItems().get(0));

        unitTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                    updateUnitChoiceBoxVisibility(newValue, formalUnit, informalUnit));
    }

    /**
     * Updates the visibility of the unit choice box based on the selected unit type.
     * @param unitType the selected unit type
     * @param formalUnit the localized "Formal" string
     * @param informalUnit the localized "Informal" string
     */
    public void updateUnitChoiceBoxVisibility(String unitType, String formalUnit, String informalUnit) {
        if (unitType == null) return;

        if (unitType.equals(formalUnit)) {
            unitChoiceBox.setVisible(true);
            infoLabel.setVisible(false);
        } else if (unitType.equals(informalUnit)) {
            unitChoiceBox.setVisible(false);
            infoLabel.setVisible(true);
        }
    }

    /**
     * Create RecipeIngredient entity based on user input. Close the popup and
     * return information to MainCtrl.
     * @return the created RecipeIngredient instance
     */
    @FXML
    private void saveButtonClicked() {
        ResourceBundle props = getTranslationsBundle();
        System.out.println("User saved ingredient to recipe.");

        // Get selected ingredient
        Ingredient ingredient = ingredientChoiceBox.getSelectionModel().getSelectedItem();

        // Check for duplicates using localized alert
        if (duplicateIngredient(recipe, ingredient) && isAdd) {
            showError(
                props.getString("alert.duplicateIngredient"),
                props.getString("alert.duplicateIngredient.body")
            );
            return;
        }

        // Get amount & create recipeIngredient
        String type = unitTypeChoiceBox.getValue();
        String formalUnit = props.getString("label.formalUnit");

        if (type.equals(formalUnit)) {
            String input = amountTextField.getText();
            if (validateFormalAmount(input)) {
                double amount = Double.parseDouble(input);
                recipeIngredient = RecipeIngredient.formal(
                    recipe,
                    ingredient,
                    amount,
                    unitChoiceBox.getValue()
                );
                closeWindow();
            }
        } else {
            String informal = amountTextField.getText();
            recipeIngredient = RecipeIngredient.informal(
                recipe,
                ingredient,
                informal
            );
            closeWindow();
        }
    }



    /**
     * Checks if an ingredient is already added to a recipe.
     * @param recipe the recipe to be checked for duplicate ingredients
     * @param ingredient the ingredient
     * @return true if it is already contained, false if not
     */
    public boolean duplicateIngredient(Recipe recipe, Ingredient ingredient) {
        if (recipe == null || recipe.getIngredients() == null) {
            return false;
        }
        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri.getIngredient().equals(ingredient)) {
                return true;
            }
        }

        return false;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    /**
     * Validates the inputted formal amount.
     * @param text the user input for formal amount
     * @return true if amount is a positive number
     */
    public boolean validateFormalAmount(String text) {
        double amount;
        ResourceBundle props = getTranslationsBundle();

        try {
            amount = Double.parseDouble(text);
            if (amount < 0) {
                showError(
                    props.getString("alert.negativeAmount"),
                    props.getString("alert.negativeAmount.body")
                );
            }
            return (amount >= 0);
        } catch (NumberFormatException e) {
            showError(
                props.getString("alert.illegalInput"),
                props.getString("alert.illegalInput.body")
            );
            return false;
        }
    }


    /**
     * Discards all edits and closes popup.
     */
    @FXML
    private void cancelButtonClicked() {
        recipeIngredient = null;

        System.out.println("User canceled adding ingredient.");
        closeWindow();
    }

    public RecipeIngredient getRecipeIngredient() {
        return recipeIngredient;
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        stage.close();
    }

    /**
     * Creates a new ingredient.
     */
    @FXML
    private void addNewIngredient() {
        showNutritionWindow();
    }

    /**
     * Makes sure the window with the nutrition information is opened once the
     * nutritional values button is pressed
     */
    @FXML
    void showNutritionWindow() {
        ResourceBundle props = getTranslationsBundle();
        if (nutritionScene == null) {
            System.err.println("Nutrition scene not initialized!");
            return;
        }

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle(props.getString("label.nutritionalValuesTitle"));
        secondaryStage.setScene(nutritionScene);
        secondaryStage.initOwner(addRecipeIngredientScene.getWindow());
        secondaryStage.showAndWait();

        populateIngredientDropdown();
    }

    private void populateIngredientDropdown() {
        ResourceBundle props = getTranslationsBundle();
        try {
            ingredientChoiceBox.getItems().clear();
            ingredientChoiceBox
                .getItems()
                .addAll(ingredientServerUtils.getAllIngredients());
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            showError(props.getString("alert.problemWithIngredients"),
                    props.getString("alert.problemWithIngredients.body"));
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

    /**
     * Translates this ui
     */
    @FXML
    public void loadLanguages() {
        ResourceBundle props = getTranslationsBundle();

        addIngredientLabel.setText(props.getString("label.addIngredientButton"));
        selectIngredient.setText(props.getString("label.selectIngredient"));
        selectUnit.setText(props.getString("label.selectUnit"));
        enterAmount.setText(props.getString("label.enterAmount"));
        infoLabel.setText(props.getString("label.informalValueInfo"));
        pleaseAddIngredientLabel.setText(props.getString("label.addIngredientPlease"));
        saveButton.setText(props.getString("label.saveButton"));
        cancelButton.setText(props.getString("label.cancelButton"));

        // Update unitTypeChoiceBox items if you want them translatable too
        unitTypeChoiceBox.getItems().clear();
        unitTypeChoiceBox.getItems().addAll(
                props.getString("label.formalUnit"),    
                props.getString("label.informalUnit")   
        );
        unitTypeChoiceBox.setValue(unitTypeChoiceBox.getItems().get(0));
    }


    /**
     * Utility method to get the ResourceBundle based on the saved language
     */
    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return ResourceBundle.getBundle(
                    "client.scenes.Translations",
                    new java.util.Locale(languageCode)
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load language bundle for code: " + configService.getLanguage());
            return ResourceBundle.getBundle("client.scenes.Translations", java.util.Locale.ENGLISH);
        }
    }


    public void setIngredient(Long id) {
        this.ingredientId = id;
    }

    private Ingredient getIngredient(Long id) {
        for (Ingredient i : ingredientChoiceBox.getItems()) {
            if (i.getId().equals(id)) {
                return i;
            }
        }

        return null;
    }
    
}
