package client.scenes;

import client.utils.IngredientServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddRecipeIngredientCtrl {
    private Scene nutritionScene;
    private Scene addRecipeIngredientScene;

    private Recipe recipe;
    private RecipeIngredient recipeIngredient;

    private final IngredientServerUtils ingredientServerUtils;

    @Inject
    public AddRecipeIngredientCtrl(
            IngredientServerUtils ingredientServerUtils) {
        this.ingredientServerUtils = ingredientServerUtils;
    }

    // UI Elements
    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ChoiceBox<Unit> unitChoiceBox;

    @FXML
    private ChoiceBox<String> unitTypeChoiceBox;

    @FXML
    private ChoiceBox<Ingredient> ingredientChoiceBox;

    @FXML
    private TextField amountTextField;

    @FXML
    private Label infoLabel;

    @FXML
    private Button newIngredient;

    @FXML
    private Label pleaseAddIngredientLabel;

    /**
     * Initialize the scenes.
     * @param nutritionScene the nutrition scenes to add a global ingredient
     * @param addRecipeIngredientScene the current add recipe-ingredient scene
     */
    public void initScenes(Scene nutritionScene, Scene addRecipeIngredientScene) {
        this.nutritionScene = nutritionScene;
        this.addRecipeIngredientScene = addRecipeIngredientScene;
    }

    /**
     * The ChoiceBoxes have the default value of the first element. Explanation
     * of unitTypeChoiceBox: selectionModel - in the case of a ChoiceBox it is a
     * SingleSelectionModel selectedItemProperty - indicates the currently
     * selected item in the selection model addListener - function that runs
     * whenever a change is observed
     */
    @FXML
    public void initialize() {
        infoLabel.setVisible(false);
        pleaseAddIngredientLabel.setVisible(false);

        populateIngredientDropdown();

        if (!ingredientServerUtils.getAllIngredients().isEmpty()) {
            ingredientChoiceBox.setValue(ingredientChoiceBox.getItems().get(0));
        } else {
            showError("No ingredients", "Please add an ingredient.");
            pleaseAddIngredientLabel.setVisible(true);
        }

        unitTypeChoiceBox.getItems().clear();
        unitTypeChoiceBox.getItems().addAll("Formal", "Informal");
        unitTypeChoiceBox.setValue(unitTypeChoiceBox.getItems().get(0));

        unitChoiceBox.getItems().clear();
        unitChoiceBox.setItems(FXCollections.observableArrayList(Unit.values()));
        unitChoiceBox.setValue(unitChoiceBox.getItems().get(0));

        unitTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    updateUnitChoiceBoxVisibility(newValue);
                });
    }

    /**
     * Updates the visibility of the unit choice box based on the selected unit
     * type.
     * @param unitType the selected unit type ("Formal" or "Informal")
     */
    public void updateUnitChoiceBoxVisibility(String unitType) {

        if(unitType == null) {
            return;
        }

        if (unitType.equals("Formal")) {
            unitChoiceBox.setVisible(true);
            infoLabel.setVisible(false);
        } else if (unitType.equals("Informal")) {
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
        System.out.println("User saved ingredient to recipe.");

        // Get ingredient
        Ingredient ingredient = ingredientChoiceBox.getSelectionModel().getSelectedItem();

        // Get amount & create recipeIngredient
        String type = unitTypeChoiceBox.getValue();
        if (type.equals("Formal")) {
            String input = amountTextField.getText();
            if (validateFormalAmount(input)) {
                double amount = Double.parseDouble(input);
                recipeIngredient = RecipeIngredient.formal(recipe,
                        ingredient, amount, unitChoiceBox.getValue());
                closeWindow();
            }
        } else {
            String informal = amountTextField.getText();
            recipeIngredient = RecipeIngredient.informal(recipe,
                    ingredient, informal);
            closeWindow();
        }
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

        try {
            amount = Double.parseDouble(text);
            if (amount < 0)
                showError("Must be Positive", "Please enter a positive number!");

            return (amount >= 0);
        } catch (NumberFormatException e) {
            showError("Wrong Format", "Please input a number!");
        }

        return false;
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
        if (nutritionScene == null) {
            System.err.println("Nutrition scene not initialized!");
            return;
        }

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Nutritional Values");
        secondaryStage.setScene(nutritionScene);
        secondaryStage.initOwner(addRecipeIngredientScene.getWindow());
        secondaryStage.showAndWait();

        populateIngredientDropdown();
    }

    private void populateIngredientDropdown() {
        try {
            ingredientChoiceBox.getItems().clear();
            ingredientChoiceBox.getItems().addAll(
                    ingredientServerUtils.getAllIngredients());
        } catch (Exception e) {
            showError("Problem with Ingredients",
                    "Something went wrong with retrieving ingredients!");
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
