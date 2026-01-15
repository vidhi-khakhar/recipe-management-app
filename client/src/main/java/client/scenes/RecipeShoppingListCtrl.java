package client.scenes;

import java.util.ArrayList;
import java.util.List;

import client.MyFXML;
import client.shopping.RecipeIngredientData;
import client.shopping.RecipeShoppingService;
import commons.Recipe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Pair;

import com.google.inject.Inject;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.SpinnerValueFactory;

/**
 * Controller for the Recipe Shopping List window.
 * Allows users to review, edit, and scale recipe ingredients before adding them to the shopping list.
 */
public class RecipeShoppingListCtrl {

    @FXML
    private Button addShoppingItemButton;

    @FXML
    private Button addToShoppingListButton;

    @FXML
    private Button cancelButton;

    @FXML
    private VBox shoppingList;

    @FXML
    private ScrollPane shoppingScrollPane;

    @FXML
    private Spinner<Integer> servingsSpinner;

    @FXML
    private Label recipeTitleLabel;

    private final RecipeShoppingService recipeShoppingService;
    private final MyFXML myFXML;
    private Recipe currentRecipe;
    private int originalServings;
    private List<RecipeIngredientData> ingredients;
    private List<IngredientItemCtrl> ingredientCtrls;

    /**
     * Constructor for dependency injection.
     * @param recipeShoppingService service for recipe shopping operations
     * @param myFXML FXML loader utility
     */
    @Inject
    public RecipeShoppingListCtrl(RecipeShoppingService recipeShoppingService, MyFXML myFXML) {
        this.recipeShoppingService = recipeShoppingService;
        this.myFXML = myFXML;
        this.ingredients = new ArrayList<>();
        this.ingredientCtrls = new ArrayList<>();
    }

    /**
     * Initializes the controller with a recipe
     * This should be called after FXML loading
     * 
     * @param recipe the recipe to display ingredients for
     */
    public void initializeWithRecipe(Recipe recipe) {
        this.currentRecipe = recipe;
        this.originalServings = recipe.getServings();
        
        // Setup UI
        recipeTitleLabel.setText("Recipe: " + recipe.getTitle());
        servingsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, originalServings));
        
        // Load ingredients
        loadIngredients();
        
        // Setup spinner listener for real-time scaling
        servingsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                scaleIngredientsToServings(newValue);
            }
        });
    }

    /**
     * Loads ingredients from the recipe and displays them
     */
    private void loadIngredients() {
        if (currentRecipe == null) return;
        
        try {
            ingredients = recipeShoppingService.getRecipeIngredients(currentRecipe);
            displayIngredients();
        } catch (Exception e) {
            showError("Failed to load ingredients: " + e.getMessage());
        }
    }

    /**
     * Displays the current list of ingredients
     */
    private void displayIngredients() {
        shoppingList.getChildren().clear();
        ingredientCtrls.clear();

        for (RecipeIngredientData ingredient : ingredients) {
            try {
                // Load ingredient item component
                Pair<IngredientItemCtrl, Parent> loader = myFXML.load(IngredientItemCtrl.class, 
                    "client", "scenes", "IngredientItem.fxml");
                IngredientItemCtrl ctrl = loader.getKey();
                Parent ingredientNode = loader.getValue();
                
                // Setup the controller
                ctrl.setIngredientData(ingredient.copy());
                ctrl.setOnRemove(() -> removeIngredient(ingredient));
                ctrl.setOnUpdate(updatedData -> {
                    ingredient.setName(updatedData.getName());
                    ingredient.setAmount(updatedData.getAmount());
                    ingredient.setUnit(updatedData.getUnit());
                    ingredient.setFormal(updatedData.isFormal());
                    ingredient.setInformal(updatedData.getInformal());
                });
                ctrl.setOnChange(this::validateIngredients);
                
                // Add to UI
                shoppingList.getChildren().add(ingredientNode);
                ingredientCtrls.add(ctrl);
            } catch (Exception e) {
                System.err.println("Failed to create ingredient item: " + e.getMessage());
            }
        }
    }

    /**
     * Scales all formal ingredients to the specified number of servings
     * 
     * @param targetServings the target number of servings
     */
    private void scaleIngredientsToServings(int targetServings) {
        if (ingredients.isEmpty()) return;

        try {
            List<RecipeIngredientData> scaledIngredients =
                recipeShoppingService.scaleIngredients(ingredients, originalServings, targetServings);

            // Update the ingredients list with scaled values
            for (int i = 0; i < ingredients.size() && i < scaledIngredients.size(); i++) {
                RecipeIngredientData original = ingredients.get(i);
                RecipeIngredientData scaled = scaledIngredients.get(i);
                if (original.isFormal() && scaled.isFormal()) {
                    original.setAmount(scaled.getAmount());
                }
            }

            // Update UI with scaled values
            for (int i = 0; i < ingredientCtrls.size() && i < scaledIngredients.size(); i++) {
                IngredientItemCtrl ctrl = ingredientCtrls.get(i);
                RecipeIngredientData scaledData = scaledIngredients.get(i);

                // Update only formal ingredients to avoid losing user edits
                RecipeIngredientData currentData = ctrl.getIngredientData();
                if (currentData.isFormal() && scaledData.isFormal()) {
                    ctrl.setIngredientData(scaledData);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to scale ingredients: " + e.getMessage());
        }
    }

    /**
     * Removes an ingredient from the list
     * 
     * @param ingredient the ingredient to remove
     */
    private void removeIngredient(RecipeIngredientData ingredient) {
        ingredients.remove(ingredient);
        displayIngredients();
    }

    /**
     * Adds a new custom ingredient.
     * 
     * @param event the action event
     */
    @FXML
    private void addShoppingItem(ActionEvent event) {
        try {
            int currentServings = servingsSpinner.getValue();
            double scaleFactor = (double) currentServings / originalServings;

            // Create new ingredient
            RecipeIngredientData newIngredient = new RecipeIngredientData();
            newIngredient.setName("New Ingredient");
            newIngredient.setAmount(1.0 * scaleFactor);
            newIngredient.setUnit("unit");
            newIngredient.setFormal(true);
            newIngredient.setCustomAdded(true);

            // Add to list
            ingredients.add(newIngredient);

            // Scale all ingredients to current servings and redisplay
            scaleIngredientsToServings(currentServings);
            displayIngredients();

            // Focus on the new ingredient for editing
            if (!ingredientCtrls.isEmpty()) {
                ingredientCtrls.get(ingredientCtrls.size() - 1).focusNameField();
            }
        } catch (Exception e) {
            showError("Failed to add ingredient: " + e.getMessage());
        }
    }

    /**
     * Adds all ingredients to the shopping list
     * 
     * @param event the action event
     */
    @FXML
    private void addToShoppingList(ActionEvent event) {
        if (currentRecipe == null) {
            showError("No recipe selected");
            return;
        }
        
        // Validate all ingredients
        if (!validateAllIngredients()) {
            showError("Please fix the highlighted ingredient fields before adding to shopping list");
            return;
        }
        
        try {
            // Get current values from UI
            List<RecipeIngredientData> currentIngredients = getCurrentIngredientData();
            
            // Get current servings
            int targetServings = servingsSpinner.getValue();
            
            // Convert to shopping items and add to list
            recipeShoppingService.addToShoppingList(
                recipeShoppingService.convertToShoppingItems(
                    currentIngredients, originalServings, targetServings, currentRecipe.getTitle(), true));
            
            // Close window
            ((Node) event.getSource()).getScene().getWindow().hide();
            
        } catch (Exception e) {
            showError("Failed to add to shopping list: " + e.getMessage());
        }
    }

    /**
     * Cancels the operation and closes the window.
     * 
     * @param event the action event
     */
    @FXML
    private void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Gets the current ingredient data from the UI
     * 
     * @return list of current ingredient data
     */
    private List<RecipeIngredientData> getCurrentIngredientData() {
        List<RecipeIngredientData> currentData = new ArrayList<>();
        
        for (IngredientItemCtrl ctrl : ingredientCtrls) {
            RecipeIngredientData data = ctrl.getIngredientData();
            if (data != null && !data.getName().trim().isEmpty()) {
                currentData.add(data);
            }
        }
        
        return currentData;
    }

    /**
     * Validates all ingredients.
     * 
     * @return true if all ingredients are valid
     */
    private boolean validateAllIngredients() {
        boolean allValid = true;
        
        for (IngredientItemCtrl ctrl : ingredientCtrls) {
            boolean valid = ctrl.validateIngredient();
            allValid = allValid && valid;
        }
        
        return allValid;
    }

    /**
     * Validates ingredients and updates UI state.
     */
    private void validateIngredients() {
        // This can be used for real-time validation feedback if needed
        // For now, we'll validate on submission
    }

    /**
     * Shows an error message dialog
     * 
     * @param message the error message
     */
    private void showError(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows a success message dialog.
     * 
     * @param message the success message
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
