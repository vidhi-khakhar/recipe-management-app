package client.scenes;

import client.utils.IngredientServerUtils;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddRecipeIngredientCtrl {
    private Recipe recipe;
    private RecipeIngredient recipeIngredient;

    private final IngredientServerUtils ingredientServerUtils = new IngredientServerUtils();

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

    /**
     * The ChoiceBoxes have the default value of the first element.
     * Explanation of unitTypeChoiceBox:
     *      selectionModel - in the case of a ChoiceBox it is a SingleSelectionModel
     *      selectedItemProperty - indicates the currently selected item in the selection model
     *      addListener - function that runs whenever a change is observed
     */
    @FXML
    public void initialize() {
        // Test ingredient, lacking all global ingredients
        ingredientChoiceBox.getItems().addAll(
                ingredientServerUtils.getAllIngredients());
        if (!ingredientServerUtils.getAllIngredients().isEmpty()) {
            ingredientChoiceBox.setValue(ingredientChoiceBox.getItems().get(0));
        }

        unitTypeChoiceBox.getItems().addAll("Formal", "Informal");
        unitTypeChoiceBox.setValue(unitTypeChoiceBox.getItems().get(0));

        unitChoiceBox.setItems(FXCollections.observableArrayList(Unit.values()));
        unitChoiceBox.setValue(unitChoiceBox.getItems().get(0));

        unitTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    updateUnitChoiceBoxVisibility(newValue);
                });
    }

    public void updateUnitChoiceBoxVisibility(String unitType) {
        if (unitType.equals("Formal")) {
            unitChoiceBox.setVisible(true);
        } else if (unitType.equals("Informal")) {
            unitChoiceBox.setVisible(false);
        }
    }

    // Button Methods

    /**
     * Create RecipeIngredient entity based on user input.
     * Close the popup and return information to MainCtrl.
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
            double amount = Double.parseDouble(amountTextField.getText());
            recipeIngredient = RecipeIngredient.formal(recipe,
                    ingredient, amount, unitChoiceBox.getValue());
        } else {
            String informal = amountTextField.getText();
            recipeIngredient = RecipeIngredient.informal(recipe,
                    ingredient, informal);
        }

        System.out.println("User saved ingredient to recipe.");
        closeWindow();
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
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
    }

}
