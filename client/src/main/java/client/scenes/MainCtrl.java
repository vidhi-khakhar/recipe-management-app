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
import commons.RecipeCollection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainCtrl {
    private Stage primaryStage;
    private Scene nutritionScene;
    private Scene recipeAddScene;

    private final RecipeServerUtils recipeServerUtils;
    private final ObservableList<Recipe> recipesObservableList = FXCollections.observableArrayList();

    // Changed to initScenes since initialize has a different purpose
    public void initScenes(Stage primaryStage, Scene mainUserInterfaceScene, Scene nutritionScene, Scene recipeAddScene) {
        this.primaryStage = primaryStage;
        this.nutritionScene = nutritionScene; // store it for later
        this.recipeAddScene = recipeAddScene; // store it for later
        primaryStage.show();
    }
    @Inject
    public MainCtrl(RecipeServerUtils recipeServerUtils) {
        this.recipeServerUtils = recipeServerUtils;
    }

    @FXML
    public void initialize() {
        sidebarList.setItems(recipesObservableList);

        // Makes it so that only the title is visible in the list
        sidebarList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Recipe item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        sidebarList.getSelectionModel().selectedItemProperty().addListener((obs, oldRecipe, newRecipe) -> {
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
            // Stores the recipes from the database in a list
            List<Recipe> recipesFromDb = recipeServerUtils.getAllRecipes();

            // Adds the recipes from the recipesFromDb list and adds it to the observable list
            recipesObservableList.addAll(recipesFromDb);
        } catch (Exception e) {
            System.out.println("Failed to load recipes: " + e.getMessage());
        }
    }


    private void onRecipeSelected(Recipe recipe) {
        titleRecipeLabel.setText(recipe.getTitle());
        //TODO showIngredients(recipe);
        //TODO showInstructions(recipe);
    }

    public void addRecipe(Recipe recipe) {
        // adds the recipe object to the list!
        recipesObservableList.add(recipe);
    }

    @FXML
    private void recipeRemoveButtonClicked() {
        Recipe selected = sidebarList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            System.out.println("No recipe selected!");
            return;
        }

        removeRecipe(selected);
        clearUI();
        sidebarList.getSelectionModel().clearSelection();

    }

    public void removeRecipe(Recipe recipe) {
        System.out.println("Removing recipe [" + recipe.getRecipeId() + "] " + recipe.getTitle());
        // removes the recipe entity from database
        recipeServerUtils.deleteRecipe(recipe.getRecipeId());
        // removes the recipe object to the list!
        recipesObservableList.remove(recipe);
    }

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
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void refreshButtonClicked() {
        System.out.println("Refreshing button clicked!");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void ingredientAddButtonClicked() {
        System.out.println("User wants to add an ingredient.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void ingredientRemoveButtonClicked() {
        System.out.println("User wants to remove an ingredient.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
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
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void editIngredientButtonClicked() {
        System.out.println("Editing selected item...");
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