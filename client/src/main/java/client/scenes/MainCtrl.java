/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.RecipeServerUtils;
import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeIngredient;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainCtrl {

    private Stage primaryStage;
    private Scene nutritionScene;
    private Scene recipeAddScene;
    private Scene addRecipeIngredientScene;
    private AddRecipeIngredientCtrl addRecipeIngredientCtrl;

    private final RecipeServerUtils recipeServerUtils;
    private final ObservableList<Recipe> recipesObservableList =
        FXCollections.observableArrayList();

    /**
     * Initializes the scenes
     *
     * @param primaryStage             the main stage
     * @param mainUserInterfaceScene   the main UI scene
     * @param nutritionScene           the nutrition scene
     * @param recipeAddScene           the add recipe scene
     * @param addRecipeIngredientScene the add ingredient to recipe scene
     * @param addRecipeIngredientCtrl  the add ingredient to recipe controller
     */
    public void initScenes(
        Stage primaryStage,
        Scene mainUserInterfaceScene,
        Scene nutritionScene,
        Scene recipeAddScene,
        Scene addRecipeIngredientScene,
        AddRecipeIngredientCtrl addRecipeIngredientCtrl
    ) {
        this.primaryStage = primaryStage;
        this.nutritionScene = nutritionScene; // store it for later
        this.recipeAddScene = recipeAddScene; // store it for later
        this.addRecipeIngredientScene = addRecipeIngredientScene;
        this.addRecipeIngredientCtrl = addRecipeIngredientCtrl;

        primaryStage.show();
    }

    /**
     * The constructor that connects the ServerUtils to this controller
     * @param recipeServerUtils the utility class responsible for server communication
     */
    @Inject
    public MainCtrl(RecipeServerUtils recipeServerUtils) {
        this.recipeServerUtils = recipeServerUtils;
    }

    /**
     * Initializes the main UI controller.
     * <p>
     * This method is automatically called by the FXML loader after the
     * controller's root element has been completely processed. It sets up
     * the ListView `sidebarList` to use `recipesObservableList` as its data
     * source and configures a custom cell factory so that only the recipe
     * title is displayed in the list.
     * </p>
     */
    @FXML
    public void initialize() {
        sidebarList.setItems(recipesObservableList);

        // Makes it so that only the title is visible in the list
        sidebarList.setCellFactory(lv ->
            new ListCell<>() {
                @Override
                protected void updateItem(Recipe item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            }
        );

        // The listeners for the selected recipe
        sidebarList
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((obs, oldRecipe, newRecipe) -> {
                if (newRecipe != null) {
                    onRecipeSelected(newRecipe);
                }
            });

        loadRecipesFromDatabase();
    }

    /**
     * Loads all recipes from the database. That way once the UI is opened, all
     * previously stored recipes are displayed.
     */
    private void loadRecipesFromDatabase() {
        try {
            // Save the currently selected recipe before clearing
            Recipe selectedRecipe = sidebarList
                .getSelectionModel()
                .getSelectedItem();

            // Stores the recipes from the database in a list
            List<Recipe> recipesFromDb = recipeServerUtils.getAllRecipes();

            // Clear existing recipes
            recipesObservableList.clear();
            // Adds the recipes from the recipesFromDb list and adds it to the observable list
            recipesObservableList.addAll(recipesFromDb);

            // Reselect previously selected recipe (if there was any)
            if (selectedRecipe != null) {
                for (Recipe recipe : recipesFromDb) {
                    if (
                        recipe
                            .getRecipeId()
                            .equals(selectedRecipe.getRecipeId())
                    ) {
                        sidebarList.getSelectionModel().select(recipe);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to load recipes: " + e.getMessage());
        }
    }

    /**
     * Changes the UI according to the selected recipe
     * @param recipe the recipe that is selected
     */
    private void onRecipeSelected(Recipe recipe) {
        titleRecipeLabel.setText(recipe.getTitle());
        //TODO showIngredients(recipe);
        //TODO showInstructions(recipe);
    }

    /**
     * Adds the recipe to the list that is displayed by the UI.
     * @param recipe the to be added recipe.
     */
    public void addRecipe(Recipe recipe) {
        // adds the recipe object to the list!
        recipesObservableList.add(recipe);
    }

    /**
     * Determines which recipe is selected and passes it to the helper methods.
     */
    @FXML
    private void recipeRemoveButtonClicked() {
        Recipe selected = sidebarList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No recipe selected!");
            return;
        }

        if (!deleteConfirmation()) {
            return;
        }

        removeRecipe(selected);
        clearUI();
        sidebarList.getSelectionModel().clearSelection();
    }

    /**
     * Opens an alert when user clicks the recipe deletion button.
     * @return true if user confirms the deletion
     *         false if user cancels the deletion
     */
    private boolean deleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to delete this recipe?");

        // Wait for the conformation from the user
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If confirmed
            return true;
        } else {
            // If cancelled
            return false;
        }
    }

    /**
     * Called by the `recipeRemoveButtonClicked()` method
     * Removes a recipe from the database and the list that is displayed by the UI
     * @param recipe the to be removed recipe.
     */
    public void removeRecipe(Recipe recipe) {
        System.out.println(
            "Removing recipe [" +
                recipe.getRecipeId() +
                "] " +
                recipe.getTitle()
        );
        // removes the recipe entity from database
        recipeServerUtils.deleteRecipe(recipe.getRecipeId());
        // removes the recipe object to the list!
        recipesObservableList.remove(recipe);
    }

    /**
     * Changes all the UI elements to be either removed or have default text
     */
    public void clearUI() {
        titleRecipeLabel.setText("No recipe selected!");
        //TODO remove ingredients and instruction step
    }

    /**
     * The placeholder for the search functionality
     * It checks if there is a query typed in the searchbar and then prints the query
     * to the console if it exists,
     * otherwise it would print "Please enter a query."
     */
    @FXML
    private void searchButtonClicked() {
        String query = searchBar.getText();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a query.");
        } else {
            System.out.println("Searching for: " + query);
            // Actual functionality should be added here!
        }
    }

    /**
     * This method prints out the button name + "is clicked!" to the console
     * when a button is clicked in the UI.
     * @param event the ActionEvent triggered when the button is clicked.
     */
    @FXML
    private void handleClick(ActionEvent event) {
        Button btn = (Button) event.getSource();
        System.out.println(btn.getId() + " is clicked!");
    }

    /**
     * Refreshes the recipe list by loading all recipes from the database
     */
    @FXML
    private void refreshButtonClicked() {
        loadRecipesFromDatabase();
    }

    /**
     * Adds an ingredient to a recipe.
     */
    @FXML
    private void ingredientAddButtonClicked() throws IOException {
        Recipe selectedRecipe = sidebarList
            .getSelectionModel()
            .getSelectedItem();
        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }

        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        showAddRecipeIngredientWindow();

        RecipeIngredient newIngredient =
            addRecipeIngredientCtrl.getRecipeIngredient();
        if (newIngredient != null) {
            addRecipeIngredientToUI(newIngredient);
        }
    }

    private void addRecipeIngredientToUI(RecipeIngredient recipeIngredient) {
        // To create a new row
        HBox ingredientItem = new HBox();
        ingredientItem.setAlignment(Pos.CENTER_LEFT);
        ingredientItem.setSpacing(8.0);
        ingredientItem.getStyleClass().add("recipe-item");

        Label itemLabel = new Label(recipeIngredient.toString());
        itemLabel.setStyle("-fx-padding: 0 0 0 9;"); // <Insets left="9.0">

        // To throw the buttons to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit button
        Button editButton = new Button("edit");
        editButton.setOnAction(event -> {
            try {
                editIngredientButtonClicked(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Remove button
        Button removeButton = new Button("remove");
        removeButton.setOnAction(this::ingredientRemoveButtonClicked);

        // Put it all into HBox
        ingredientItem
            .getChildren()
            .addAll(itemLabel, spacer, editButton, removeButton);

        // Put the HBox (the row) in the ingredientList
        ingredientList.getChildren().add(ingredientItem);
    }

    /**
     * Removes an ingredient from a recipe.
     */
    @FXML
    private void ingredientRemoveButtonClicked(ActionEvent event) {
        Button removeButton = (Button) event.getSource(); // To identify the button of which ingredient was clicked
        HBox ingredientToRemove = (HBox) removeButton.getParent(); // To delete the whole row
        ingredientList.getChildren().remove(ingredientToRemove);

        System.out.println("Removed ingredient.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked.
     */
    @FXML
    private void preparationAddButtonClicked() {
        System.out.println("User wants to add a preparation.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void preparationRemoveButtonClicked() {
        System.out.println("User wants to remove a preparation.");
    }

    /**
     * Edit an ingredient of a selected recipe.
     */
    @FXML
    private void editIngredientButtonClicked(ActionEvent event)
        throws IOException {
        Button editButton = (Button) event.getSource();
        HBox ingredientToEdit = (HBox) editButton.getParent();
        Label ingredientLabel = (Label) ingredientToEdit.getChildren().get(0);

        Recipe selectedRecipe = sidebarList
            .getSelectionModel()
            .getSelectedItem();
        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }

        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        showAddRecipeIngredientWindow();
        ingredientLabel.setText(
            addRecipeIngredientCtrl.getRecipeIngredient().toString()
        );

        System.out.println("Edited ingredient.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void editPreparationButtonClicked() {
        System.out.println("Editing selected item...");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void editRecipeTitleButtonClicked() {
        System.out.println("Editing recipe title...");
    }

    /**
     * This is a placeholder a helper method for further list management
     * Method prints out a message when the button is clicked
     * @param ingredient the ingredient to be added
     */
    @FXML
    private void addIngredientToList(String ingredient) {
        System.out.println("Added ingredient: " + ingredient);
    }

    /**
     * This is a placeholder a helper method for further list management
     * Method prints out a message when the button is clicked
     * @param step the step to be added
     */
    @FXML
    private void addPreparationStep(String step) {
        System.out.println("Added preparation step: " + step);
    }

    /**
     * Opens the add ingredient to recipe popup window.
     */
    @FXML
    void showAddRecipeIngredientWindow() {
        if (addRecipeIngredientScene == null) {
            System.err.println("AddRecipeIngredientScene not initialized!");
            return;
        }

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Add Ingredient to Recipe");
        secondaryStage.setScene(addRecipeIngredientScene);
        secondaryStage.setMaxWidth(600);
        secondaryStage.setMaxHeight(430);
        secondaryStage.initOwner(primaryStage);
        secondaryStage.showAndWait();
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
        secondaryStage.initOwner(primaryStage); // makes it modal to main window if desired
        secondaryStage.show();
    }

    /**
     * Makes sure the window with the add recipe functionality is opened once the
     * Add button is pressed.
     */
    @FXML
    void showAddRecipeWindow() {
        if (recipeAddScene == null) {
            System.err.println("Recipe add scene not initialized!");
            return;
        }

        Stage recipeAddStage = new Stage();
        recipeAddStage.setTitle("Add Recipe");
        recipeAddStage.setScene(recipeAddScene);
        recipeAddStage.initOwner(primaryStage);

        recipeAddStage.show();
    }

    @FXML
    private ListView<Recipe> sidebarList;

    @FXML
    private Button addIngredientButton;

    @FXML
    private Button addPreparationButton;

    @FXML
    private Button addRecipeButton;

    @FXML
    private Button editItemButton;

    @FXML
    private Button editRecipeTitleButton;

    @FXML
    private VBox ingredientList;

    @FXML
    private Label itemLabel;

    @FXML
    private VBox preparationList;

    @FXML
    private Button refreshButton;

    @FXML
    private Button removeItemButton;

    @FXML
    private Button removeRecipeButton;

    @FXML
    private Button nutritionalValueButton;

    @FXML
    private TextField searchBar;

    @FXML
    private Button searchButton;

    @FXML
    private Label titleRecipeLabel;
}
