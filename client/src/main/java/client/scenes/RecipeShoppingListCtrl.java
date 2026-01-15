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
 *
 * <p>This controller manages the UI for converting recipe ingredients into shopping items,
 * supporting ingredient editing, serving size scaling, and adding to the shopping list.</p>
 *
 * <p>Key functionality includes:</p>
 * <ul>
 *   <li>Displaying recipe ingredients for review and editing</li>
 *   <li>Scaling ingredient amounts based on serving size changes</li>
 *   <li>Adding custom ingredients to the list</li>
 *   <li>Validating and converting ingredients to shopping items</li>
 *   <li>Adding ingredients to the main shopping list</li>
 * </ul>
 *
 * @author Recipe Shopping Controller
 * @version 1.0
 */
public class RecipeShoppingListCtrl {

    /**
     * Button to add a new custom ingredient to the list.
     */
    @FXML
    private Button addShoppingItemButton;

    /**
     * Button to add all ingredients to the shopping list.
     */
    @FXML
    private Button addToShoppingListButton;

    /**
     * Button to cancel the operation and close the window.
     */
    @FXML
    private Button cancelButton;

    /**
     * VBox container for displaying ingredient items.
     */
    @FXML
    private VBox shoppingList;

    /**
     * Scroll pane containing the ingredient list for scrolling.
     */
    @FXML
    private ScrollPane shoppingScrollPane;

    /**
     * Spinner for adjusting the number of servings, which scales ingredient amounts.
     * Range: 1-50 servings.
     */
    @FXML
    private Spinner<Integer> servingsSpinner;

    /**
     * Label displaying the current recipe title.
     */
    @FXML
    private Label recipeTitleLabel;

    /**
     * Service for handling recipe shopping list operations.
     */
    private final RecipeShoppingService recipeShoppingService;

    /**
     * FXML loader utility for loading and initializing FXML components.
     */
    private final MyFXML myFXML;

    /**
     * The currently loaded recipe for which ingredients are being displayed.
     */
    private Recipe currentRecipe;

    /**
     * The original number of servings from the recipe.
     */
    private int originalServings;

    /**
     * List of ingredients displayed in the UI.
     */
    private List<RecipeIngredientData> ingredients;

    /**
     * List of ingredient controllers for managing individual ingredient UI items.
     */
    private List<IngredientItemCtrl> ingredientCtrls;

    /**
     * Constructor for dependency injection.
     *
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
     * Initializes the controller with a recipe.
     * This should be called after FXML loading.
     *
     * <p>This method sets up the UI components with the recipe data, including:</p>
     * <ul>
     *   <li>Setting the recipe title in the UI</li>
     *   <li>Configuring the servings spinner with the recipe's original servings</li>
     *   <li>Setting up a listener for serving size changes to scale ingredients</li>
     *   <li>Loading and displaying the recipe ingredients</li>
     * </ul>
     *
     * @param recipe the recipe to display ingredients for
     */
    public void initializeWithRecipe(Recipe recipe) {
        this.currentRecipe = recipe;
        this.originalServings = recipe.getServings();

        // Setup UI (only if FXML fields are initialized)
        if (recipeTitleLabel != null) {
            recipeTitleLabel.setText("Recipe: " + recipe.getTitle());
        }
        if (servingsSpinner != null) {
            var spinnerFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    1, 50, originalServings);
            servingsSpinner.setValueFactory(spinnerFactory);

            // Setup spinner listener for real-time scaling
            servingsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(oldValue)) {
                    scaleIngredientsToServings(newValue);
                }
            });
        }

        // Load ingredients
        loadIngredients();
    }

    /**
     * Loads ingredients from the recipe and displays them.
     *
     * <p>Retrieves ingredients from the recipe using the shopping service
     * and calls {@link #displayIngredients()} to render them in the UI.</p>
     *
     * <p>If an error occurs during loading, an error message is displayed to the user.</p>
     */
    void loadIngredients() {
        if (currentRecipe == null) return;

        try {
            ingredients = recipeShoppingService.getRecipeIngredients(currentRecipe);
            displayIngredients();
        } catch (Exception e) {
            showError("Failed to load ingredients: " + e.getMessage());
        }
    }

    /**
     * Displays the current list of ingredients in the UI.
     *
     * <p>This method clears the existing ingredient items and creates new
     * {@link IngredientItemCtrl} instances for each ingredient in the list.
     * Each controller is configured with callbacks for remove, update, and change events.</p>
     *
     * <p>Ingredients are loaded from the IngredientItem.fxml FXML file.</p>
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
     * Scales all formal ingredients to the specified number of servings.
     *
     * <p>Calculates the scale factor based on the original servings and target servings,
     * then applies the scaling to all formal ingredients. Informal ingredients
     * are not scaled as they represent non-numeric quantities.</p>
     *
     * <p>Only formal ingredients are updated to preserve any user edits made
     * to the ingredient data.</p>
     *
     * @param targetServings the target number of servings to scale ingredients for
     */
    void scaleIngredientsToServings(int targetServings) {
        if (ingredients.isEmpty()) return;

        try {
            List<RecipeIngredientData> scaledIngredients =
                recipeShoppingService.scaleIngredients(ingredients, originalServings, targetServings);

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
     * Removes an ingredient from the list.
     *
     * <p>Removes the specified ingredient from the internal ingredients list
     * and refreshes the UI by calling {@link #displayIngredients()}.</p>
     *
     * @param ingredient the ingredient to remove from the list
     */
    void removeIngredient(RecipeIngredientData ingredient) {
        ingredients.remove(ingredient);
        displayIngredients();
    }

    /**
     * Adds a new custom ingredient to the list.
     *
     * <p>Creates a new ingredient with default values and adds it to the list.
     * The servings spinner is reset to the original value, and the new ingredient
     * is marked as custom-added.</p>
     *
     * <p>After adding, focus is placed on the new ingredient's name field
     * for immediate editing.</p>
     *
     * @param event the action event triggered by the button click (may be null)
     */
    @FXML
    void addShoppingItem(ActionEvent event) {
        try {
            // Reset servings spinner to original when adding new ingredient
            servingsSpinner.getValueFactory().setValue(originalServings);

            // Create new ingredient with base amount (no scaling)
            RecipeIngredientData newIngredient = new RecipeIngredientData();
            newIngredient.setName("New Ingredient");
            newIngredient.setAmount(1.0);
            newIngredient.setUnit("unit");
            newIngredient.setFormal(true);
            newIngredient.setCustomAdded(true);

            // Add to list
            ingredients.add(newIngredient);
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
     * Adds a new custom ingredient for testing purposes.
     *
     * <p>This overload does not require an ActionEvent and is used primarily
     * for testing scenarios.</p>
     */
    void addShoppingItem() {
        addShoppingItem(null);
    }

    /**
     * Adds all ingredients to the shopping list.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Validates all ingredient fields</li>
     *   <li>Retrieves current ingredient data from the UI</li>
     *   <li>Gets the target servings from the spinner</li>
     *   <li>Converts ingredients to shopping items</li>
     *   <li>Adds the items to the shopping list via the service</li>
     *   <li>Closes the window</li>
     * </ul>
     *
     * <p>If validation fails, an error message is shown and the operation is cancelled.</p>
     *
     * @param event the action event triggered by the button click
     */
    @FXML
    void addToShoppingList(ActionEvent event) {
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
     * @param event the action event triggered by the cancel button click
     */
    @FXML
    void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    /**
     * Gets the current ingredient data from the UI controllers.
     *
     * <p>Iterates through all ingredient controllers and retrieves their
     * current data. Only ingredients with non-empty names are included in the result.</p>
     *
     * @return list of current ingredient data from the UI
     */
    List<RecipeIngredientData> getCurrentIngredientData() {
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
     * <p>Calls the validate method on each ingredient controller to check
     * that all required fields are properly filled.</p>
     *
     * @return true if all ingredients are valid, false otherwise
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
     *
     * <p>This method is called when ingredient data changes. It can be used
     * for real-time validation feedback if needed. Currently, validation
     * is performed on submission via {@link #validateAllIngredients()}.</p>
     */
    private void validateIngredients() {
        // This can be used for real-time validation feedback if needed
        // For now, we'll validate on submission
    }

    /**
     * Shows an error message dialog.
     *
     * @param message the error message to display
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
     * @param message the success message to display
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Test accessor methods

    /**
     * Gets the current recipe for testing purposes.
     *
     * @return the current recipe
     */
    Recipe getCurrentRecipeForTest() {
        return currentRecipe;
    }

    /**
     * Sets the current recipe for testing purposes.
     *
     * @param recipe the recipe to set
     */
    void setCurrentRecipeForTest(Recipe recipe) {
        this.currentRecipe = recipe;
    }

    /**
     * Gets the original servings for testing purposes.
     *
     * @return the original number of servings
     */
    int getOriginalServingsForTest() {
        return originalServings;
    }

    /**
     * Sets the original servings for testing purposes.
     *
     * @param servings the number of servings to set
     */
    void setOriginalServingsForTest(int servings) {
        this.originalServings = servings;
    }

    /**
     * Gets the ingredients list for testing purposes.
     *
     * @return the list of ingredients
     */
    List<RecipeIngredientData> getIngredientsForTest() {
        return ingredients;
    }

    /**
     * Gets the ingredient controllers list for testing purposes.
     *
     * @return the list of ingredient controllers
     */
    List<IngredientItemCtrl> getIngredientCtrlsForTest() {
        return ingredientCtrls;
    }

    /**
     * Sets the recipe title label for testing purposes.
     *
     * @param label the label to set
     */
    void setRecipeTitleLabelForTest(javafx.scene.control.Label label) {
        this.recipeTitleLabel = label;
    }

    /**
     * Sets the servings spinner for testing purposes.
     *
     * @param spinner the spinner to set
     */
    void setServingsSpinnerForTest(javafx.scene.control.Spinner<Integer> spinner) {
        this.servingsSpinner = spinner;
    }

    /**
     * Sets the shopping list VBox for testing purposes.
     *
     * @param vbox the VBox to set
     */
    void setShoppingListForTest(javafx.scene.layout.VBox vbox) {
        this.shoppingList = vbox;
    }

    /**
     * Sets the add shopping item button for testing purposes.
     *
     * @param button the button to set
     */
    void setAddShoppingItemButtonForTest(javafx.scene.control.Button button) {
        this.addShoppingItemButton = button;
    }

    /**
     * Sets the add to shopping list button for testing purposes.
     *
     * @param button the button to set
     */
    void setAddToShoppingListButtonForTest(javafx.scene.control.Button button) {
        this.addToShoppingListButton = button;
    }

    /**
     * Sets the cancel button for testing purposes.
     *
     * @param button the button to set
     */
    void setCancelButtonForTest(javafx.scene.control.Button button) {
        this.cancelButton = button;
    }
}
