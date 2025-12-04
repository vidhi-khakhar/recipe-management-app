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

import commons.Recipe;
import commons.RecipeIngredient;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

public class MainCtrl {
    private Stage primaryStage;
     private Scene nutritionScene; 

    public void initialize(Stage primaryStage, Scene mainUserInterfaceScene, Scene nutritionScene) {
        this.primaryStage = primaryStage;
        this.nutritionScene = nutritionScene; // store it for later
        primaryStage.show();
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
    void recipeAddButtonClicked() {
        System.out.println("User wants to add a recipe.");
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void recipeRemoveButtonClicked() {
        System.out.println("User wants to remove a recipe.");
    }

    /**
     * Adds an ingredient to a recipe.
     * Method also prints out a message when the button is clicked.
     */
    @FXML
    private void ingredientAddButtonClicked() {
//        Recipe selectedRecipe = (Recipe) sidebarList.getSelectionModel().getSelectedItem();
//        if (selectedRecipe == null) {
//            System.out.println("Please select a recipe first.");
//            return;
//        }

        // Test recipe
        Recipe selectedRecipe = new Recipe("Sarmale");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddIngredientScene.fxml"));
            Parent root = loader.load();        // Constructs all JavaFX objects from FXML file

            AddIngredientCtrl controller = loader.getController();
            controller.setRecipe(selectedRecipe);

            Stage popup = new Stage();          // Create new stage to keep main UI open
            popup.initOwner(primaryStage);      // Sets the stage from which popup originates
            popup.setTitle("Add Ingredient");   // Sets title
            popup.setScene(new Scene(root));    // Loads the AddIngredientScene
            popup.showAndWait();                // Pause execution until window closed

            addRecipeIngredientToUI(controller.getRecipeIngredient());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Added ingredient.");
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
        editButton.setOnAction(this::editIngredientButtonClicked);

        // Remove button
        Button removeButton = new Button("remove");
        removeButton.setOnAction(this::ingredientRemoveButtonClicked);

        // Put it all into HBox
        ingredientItem.getChildren().addAll(itemLabel, spacer, editButton, removeButton);

        // Put the HBox (the row) in the ingredientList
        ingredientList.getChildren().add(ingredientItem);
    }

    /**
     * Removes an ingredient from a recipe.
     * Method also prints out a message when the button is clicked.
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
    private void editIngredientButtonClicked(ActionEvent event) {
        Button editButton = (Button) event.getSource();
        HBox ingredientToEdit = (HBox) editButton.getParent();
        Label ingredientLabel = (Label) ingredientToEdit.getChildren().get(0);

        // Test recipe
        Recipe selectedRecipe = new Recipe("Sarmale");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddIngredientScene.fxml"));
            Parent root = loader.load();

            AddIngredientCtrl controller = loader.getController();
            controller.setRecipe(selectedRecipe);

            Stage popup = new Stage();
            popup.initOwner(primaryStage);
            popup.setTitle("Edit Ingredient");
            popup.setScene(new Scene(root));
            System.out.println("Popup about to show");
            popup.showAndWait();

            ingredientLabel.setText(controller.getRecipeIngredient().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
    private ListView<?> sidebarList;

    @FXML
    private Label titleRecipeLabel;

}