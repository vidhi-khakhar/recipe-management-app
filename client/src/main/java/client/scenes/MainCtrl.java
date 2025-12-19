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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.inject.Inject;

import client.utils.RecipeServerUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
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
    private Scene editTitleScene;

    private AddRecipeIngredientCtrl addRecipeIngredientCtrl;

    private final RecipeServerUtils recipeServerUtils;
    private final ObservableList<Recipe> recipesObservableList = FXCollections.observableArrayList();
    private Recipe selectedRecipe;

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
    public void initScenes(Stage primaryStage, Scene mainUserInterfaceScene, Scene nutritionScene,
                           Scene recipeAddScene, Scene addRecipeIngredientScene, AddRecipeIngredientCtrl addRecipeIngredientCtrl,
                           Scene editTitleScene) {
        this.primaryStage = primaryStage;
        this.nutritionScene = nutritionScene; // store it for later
        this.recipeAddScene = recipeAddScene; // store it for later
        this.addRecipeIngredientScene = addRecipeIngredientScene;
        this.addRecipeIngredientCtrl = addRecipeIngredientCtrl;
        this.editTitleScene = editTitleScene;

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

        // The listeners for the selected recipe
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

            // Select the first recipe if available
            if (!recipesObservableList.isEmpty()) {
            sidebarList.getSelectionModel().selectFirst();
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
        this.selectedRecipe = recipe;
        titleRecipeLabel.setText(recipe.getTitle());
        showInstructions(recipe);
        //TODO showIngredients(recipe);
    }

    /**
     * Adds the recipe to the list that is displayed by the UI.
     * @param recipe the to be added recipe.
     */
    public void addRecipe(Recipe recipe) {
        // adds the recipe object to the list!
        recipesObservableList.add(recipe);
        sidebarList.getSelectionModel().select(recipe);
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

        if(!deleteConfirmation()){
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
        System.out.println("Removing recipe [" + recipe.getRecipeId() + "] " + recipe.getTitle());
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
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked
     */
    @FXML
    private void refreshButtonClicked() {
        System.out.println("Refreshing button clicked!");
    }

    /**
     * Adds an ingredient to a recipe.
     */
    @FXML
    private void ingredientAddButtonClicked() throws IOException {
        Recipe selectedRecipe = sidebarList.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }

        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        showAddRecipeIngredientWindow();

        RecipeIngredient newIngredient = addRecipeIngredientCtrl.getRecipeIngredient();
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
        ingredientItem.getChildren().addAll(itemLabel, spacer, editButton, removeButton);

        // Put the HBox (the row) in the ingredientList
        ingredientList.getChildren().add(ingredientItem);
    }

    /**
     * Removes an ingredient from a recipe.
     */
    @FXML
    private void ingredientRemoveButtonClicked(ActionEvent event) {
        Button removeButton = (Button) event.getSource();           // To identify the button of which ingredient was clicked
        HBox ingredientToRemove = (HBox) removeButton.getParent();  // To delete the whole row
        ingredientList.getChildren().remove(ingredientToRemove);

        System.out.println("Removed ingredient.");
    }

    // ALL INSTRUCTION / PREPARATION STEP METHODS BELOW HERE:

    /**
     * Displays the instructions of a recipe in the UI.
     * @param recipe the recipe whose instructions are to be displayed
     */
    private void showInstructions(Recipe recipe) {
        preparationList.getChildren().clear();

        List<String> steps = recipe.getInstructions();

        for (int i = 0; i < steps.size(); i++) {
            HBox row = createExecutionStepRow(i, steps.get(i),false);
            preparationList.getChildren().add(row);
        }
    }

    /**
     * This is a placeholder method for further development
     * Method prints out a message when the button is clicked.
     */
    @FXML
    private void preparationAddButtonClicked() {
        if (selectedRecipe == null) return;

        // Add empty step to recipe
        selectedRecipe.getInstructions().add("");

        int newIndex = selectedRecipe.getInstructions().size() - 1;
        HBox row = createExecutionStepRow(newIndex, "", true); // editable
        preparationList.getChildren().add(row);
    }

    /**
     * Creates a row for a preparation step.
     * @param index       the index of the step
     * @param initialText the initial text of the step
     * @param editable    whether the step is initially editable
     */
    private HBox createExecutionStepRow(int index, String initialText, boolean startEditable) {
        // Create a new HBox (a preparation step)
        // [ 1. ][ Label(text) ][ ✏ ][ ✖ ] when not editing
        // [ 1. ][ TextField ][ ✔ ][ ✖ ] when editing
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        //index 
        Label indexLabel = new Label((index + 1) + ".");
        indexLabel.setMinWidth(30);

        Label textLabel = new Label(initialText);
        textLabel.setPrefWidth(400);

        TextField textField = new TextField(initialText);
        textField.setPrefWidth(400);

        Button editSaveButton = new Button();
        Button deleteButton = new Button("✖");

      
        final Runnable[] switchToEdit = new Runnable[1];
        final Runnable[] saveStep = new Runnable[1];

        saveStep[0] = () -> {
        String text = textField.getText().trim();
        if (text.isEmpty()) return;

        selectedRecipe.getInstructions().set(index, text);
        textLabel.setText(text);

    
        try {
            recipeServerUtils.replaceRecipe(selectedRecipe.getRecipeId(), selectedRecipe);
        } catch (Exception e) {
            System.err.println("Failed to save recipe: " + e.getMessage());
        }

        editSaveButton.setText("✏");
        row.getChildren().setAll(indexLabel, textLabel, editSaveButton, deleteButton);

        editSaveButton.setOnAction(e -> switchToEdit[0].run());
    };

        switchToEdit[0] = () -> {
            textField.setText(textLabel.getText());
            editSaveButton.setText("✔");
            row.getChildren().setAll(indexLabel, textField, editSaveButton, deleteButton);
            textField.requestFocus();
            textField.selectAll();

            textField.setOnAction(ev -> saveStep[0].run());

            editSaveButton.setOnAction(e -> saveStep[0].run());
        };

        if (startEditable) {
            switchToEdit[0].run();
        } else {
            editSaveButton.setText("✏");
            editSaveButton.setOnAction(e -> switchToEdit[0].run());
            row.getChildren().addAll(indexLabel, textLabel, editSaveButton, deleteButton);
        }

        deleteButton.setOnAction(e -> handleDeleteStep(index));

        return row;
    }

    /**
     * Handles the deletion of a preparation step.
     * @param index the index of the step to be deleted
     */
    private void handleDeleteStep(int index) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Step");
        alert.setHeaderText("Delete this preparation step?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            selectedRecipe.getInstructions().remove(index);

            try {
                recipeServerUtils.replaceRecipe(selectedRecipe.getRecipeId(), selectedRecipe);
            } catch (Exception e) {
                System.err.println("Failed to save recipe after deleting step: " + e.getMessage());
            }

            showInstructions(selectedRecipe);
        }
    }

    
    /**
     * Edit an ingredient of a selected recipe.
     */
    @FXML
    private void editIngredientButtonClicked(ActionEvent event) throws IOException {
        Button editButton = (Button) event.getSource();
        HBox ingredientToEdit = (HBox) editButton.getParent();
        Label ingredientLabel = (Label) ingredientToEdit.getChildren().get(0);

        Recipe selectedRecipe = sidebarList.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }

        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        showAddRecipeIngredientWindow();
        ingredientLabel.setText(addRecipeIngredientCtrl.getRecipeIngredient().toString());

        System.out.println("Edited ingredient.");
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
        recipeAddStage.setResizable(false);

        recipeAddStage.show();
    }

    /**
     * Shows edit title of recipe window.
     */
    @FXML
    void showEditRecipeTitleScene() {
        if (editTitleScene == null) {
            System.err.println("Scene not initialized!");
            return;
        }

        Stage editTitleStage = new Stage();
        editTitleStage.setTitle("Edit Recipe Title");
        editTitleStage.setScene(editTitleScene);
        editTitleStage.initOwner(primaryStage);

        editTitleStage.show();
    }

    /**
     * Returns selected recipe.
     * @return selected Recipe instance
     */
    public Recipe getSelectedRecipe() {
        return sidebarList.getSelectionModel().getSelectedItem();
    }

    /**
     * Updates the title of the recipe according to user input.
     */
    @FXML
    void editRecipeTitleButtonClicked() {
        showEditRecipeTitleScene();
    }

    public void refreshSidebarList() {
        sidebarList.refresh();
    }

    public void updateTitleLabel(String title) {
        titleRecipeLabel.setText(title);
    }

    @FXML
    void cloneRecipe() {
        // TODO: Implement clone recipe
    }

    @FXML
    private ListView<Recipe> sidebarList;

    @FXML
    private Button addIngredientButton;

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

    // INSTRUCTIONS:

    @FXML
    private ScrollPane prepScrollPane;

    @FXML
    private VBox preparationList;

    @FXML
    private Button addStepButton;


}