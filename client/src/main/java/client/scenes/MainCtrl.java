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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import client.config.ConfigService;
import client.favorite.FavoriteService;
import client.search.RecipeCollection;
import client.search.SearchService;
import javafx.scene.input.KeyCode;
import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import client.RecipeFormatter;
import client.utils.RecipeServerUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
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
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainCtrl {

    private Stage primaryStage;
    private Scene nutritionScene;
    private Scene shoppingListScene;
    private Scene recipeAddScene;
    private Scene addRecipeIngredientScene;
    private Scene editTitleScene;
    private Scene recipeShoppingListScene;

    private AddRecipeIngredientCtrl addRecipeIngredientCtrl;
    private RecipeShoppingListCtrl recipeShoppingListCtrl;
    private NutritionalValueCtrl nutritionalValueCtrl;

    private final RecipeServerUtils recipeServerUtils;
    private final FavoriteService favoriteService;
    private final ObservableList<Recipe> recipesObservableList = FXCollections.observableArrayList();
    private Recipe selectedRecipe;
    private final SearchService searchService;
    private final RecipeCollection recipeCollection;

    private ConfigService configService;

    /**
     * Initializes the scenes
     *
     * @param primaryStage the main stage
     * @param mainUserInterfaceScene the main UI scene
     * @param nutritionScene the nutrition scene
     * @param recipeAddScene the add recipe scene
     * @param addRecipeIngredientScene the add ingredient to recipe scene
     * @param addRecipeIngredientCtrl the add ingredient to recipe controller
     * @param nutritionalValueCtrl the nutritional value controller
     * @param recipeShoppingListCtrl the recipe shopping list controller    
     */
    public void initScenes(Stage primaryStage, Scene nutritionScene,
            Scene recipeAddScene, Scene addRecipeIngredientScene, AddRecipeIngredientCtrl addRecipeIngredientCtrl,
            Scene editTitleScene, Scene shoppingListScene, Scene recipeShoppingListScene,
            NutritionalValueCtrl nutritionalValueCtrl,
            RecipeShoppingListCtrl recipeShoppingListCtrl) {
            
        this.primaryStage = primaryStage;
        this.nutritionScene = nutritionScene;
        this.recipeAddScene = recipeAddScene; 
        this.addRecipeIngredientScene = addRecipeIngredientScene;
        this.addRecipeIngredientCtrl = addRecipeIngredientCtrl;
        this.editTitleScene = editTitleScene;
        this.shoppingListScene = shoppingListScene;
        this.recipeShoppingListScene = recipeShoppingListScene;
        this.recipeShoppingListCtrl = recipeShoppingListCtrl;
        this.nutritionalValueCtrl = nutritionalValueCtrl;

        primaryStage.show();
    }

    /**
     * The constructor that connects the ServerUtils to this controller.
     *
     * @param recipeServerUtils the utility class responsible for server
     * communication.
     * @param favoriteService the service class responsible for favoriting
     * logic.
     */
    @Inject
    public MainCtrl(RecipeServerUtils recipeServerUtils, SearchService searchService,
                    FavoriteService favoriteService, RecipeCollection recipeCollection, ConfigService configService) {
        this.recipeServerUtils = recipeServerUtils;
        this.searchService = searchService;
        this.favoriteService = favoriteService;
        this.recipeCollection = recipeCollection;
        this.configService = configService;
    }

    /**
     * Initializes the main UI controller.
     * <p>
     * This method is automatically called by the FXML loader after the
     * controller's root element has been completely processed. It sets up the
     * ListView `sidebarList` to use `recipesObservableList` as its data source
     * and configures a custom cell factory so that only the recipe title is
     * displayed in the list.
     * </p>
     */
    @FXML
    public void initialize() {
        sidebarList.setItems(recipesObservableList);

        sidebarList.setCellFactory(lv ->
                new RecipeListCell(favoriteService)
        );

        sidebarList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldRecipe, newRecipe) -> {
                    if (newRecipe != null) {
                        onRecipeSelected(newRecipe);
                    }
                });

        //Listener for the searchBar
        searchBar.textProperty().addListener((obs, oldText, newText) -> {
            applySearch();
        });

        //Listener for esc key
        searchBar.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                searchBar.clear();
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
            Recipe previouslySelected
                    = sidebarList.getSelectionModel().getSelectedItem();

            List<Recipe> recipesFromDB = recipeServerUtils.getAllRecipes();
            List<Long> removedIDs = favoriteService.removeDeletedFavorites(recipesFromDB);

            //Check if a favorited recipe got deleted
            if(!removedIDs.isEmpty()) {
                showFavoriteRemovedAlert(removedIDs);
            }

            //Update the UI and the collection of recieps to the database version
            recipeCollection.update(recipesFromDB);

            favoriteService.removeDeletedFavorites(recipesFromDB);
            applySearch();



            if (!recipesObservableList.isEmpty()) {
                if (previouslySelected != null && recipesObservableList.contains(previouslySelected)) {
                    sidebarList.getSelectionModel().select(previouslySelected);
                } else {
                    sidebarList.getSelectionModel().selectFirst();
                }
            }else{
                sidebarList.getSelectionModel().clearSelection();
            }

        } catch (Exception e) {
            System.out.println("Failed to load recipes: " + e.getMessage());
        }
    }

    /**
     * The alert to let the user know their favorite recipe got deleted by
     * someone else.
     * @param removedIds the removed recipes' IDs
     */
    private void showFavoriteRemovedAlert(List<Long> removedIds) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Favorite deleted");
        alert.setHeaderText("A favorite recipe got deleted");
        alert.setContentText("Helaas pindakaas — one or more favorites have been removed by someone else.");
        alert.showAndWait();
    }

    /**
     * Updates the recipe stack pane based on whether there are recipes in the
     * sidebar list.
     */
    @FXML
    public void updateRecipeStackPane() {
        boolean hasRecipes = !sidebarList.getItems().isEmpty();
        System.out.println(hasRecipes);
        if (!hasRecipes) {
            noRecipePane.setVisible(true);
            allRecipePane.setVisible(false);
        } else {
            noRecipePane.setVisible(false);
            allRecipePane.setVisible(true);
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
        showIngredients(recipe);
    }

    /**
     * Adds the recipe to the list that is displayed by the UI.
     *
     * @param recipe the to be added recipe.
     */
    public void addRecipe(Recipe recipe) {
        // adds the recipe object to the list!
        recipeCollection.add(recipe);
        applySearch();
        sidebarList.getSelectionModel().select(recipe);
        updateRecipeStackPane();
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
        // If the selected recipe is a favorite, the favorite id gets removed from config
        if(favoriteService.isFavorite(selected)) {
            favoriteService.removeFavorite(selected);
        }

        updateRecipeStackPane();

        if (!recipesObservableList.isEmpty()) {
            sidebarList.getSelectionModel().selectFirst();
        }
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

        if(favoriteService.isFavorite(selectedRecipe)) {
            alert.setContentText("This is one of your favorites!");
        }

        // Wait for the conformation from the user
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // If confirmed
            return true;
        } else {
            // If canceled
            return false;
        }
    }

    /**
     * Called by the `recipeRemoveButtonClicked()` method Removes a recipe from
     * the database and the list that is displayed by the UI
     *
     * @param recipe the to be removed recipe.
     */
    public void removeRecipe(Recipe recipe) {
        System.out.println("Removing recipe ["
                + recipe.getRecipeId()
                + "] "
                + recipe.getTitle()
        );
        // removes the recipe entity from database
        recipeServerUtils.deleteRecipe(recipe.getRecipeId());
        // removes the recipe object to the list!
        recipeCollection.remove(recipe.getRecipeId());

        applySearch();
    }


    /**
     * The placeholder for the search functionality It checks if there is a
     * query typed in the searchbar and then prints the query to the console if
     * it exists, otherwise it would print "Please enter a query."
     */
    @FXML
    private void applySearch() {
        String query = searchBar.getText();
        boolean searchFavorites;

        if(favoriteS.getText() == "★"){
            searchFavorites = true;
        }else{
            searchFavorites = false;
        }

        List<Recipe> filtered = searchService.search(query, searchFavorites);

        recipesObservableList.setAll(filtered);
        updateRecipeStackPane();
    }

    @FXML
    private void searchFavorites() {
        if(favoriteS.getText() == "★"){
            favoriteS.setText("☆");
        }else{
            favoriteS.setText("★");
        }

        applySearch();
    }

    /**
     * Refreshes the recipe list by loading all recipes from the database
     */
    @FXML
    private void refreshButtonClicked() {
        configService.reload(); // this makes sure the client has the up to date config
        loadRecipesFromDatabase();
        favoriteService.loadFromConfig();
        sidebarList.refresh();
        showIngredients(sidebarList.getSelectionModel().getSelectedItem());
    }

    // RECIPE INGREDIENTS METHODS
    /**
     * Shows the ingredients for a recipe.
     */
    @FXML
    void showIngredients(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        ingredientList.getChildren().clear();
        List<RecipeIngredient> ingredients = recipe.getIngredients();

        for (RecipeIngredient ingredient : ingredients) {
            addRecipeIngredientToUI(ingredient);
        }
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
        addRecipeIngredientCtrl.onStart();
        addRecipeIngredientCtrl.initScenes(nutritionScene, addRecipeIngredientScene);
        secondaryStage.setTitle("Add Ingredient to Recipe");
        secondaryStage.setScene(addRecipeIngredientScene);
        secondaryStage.setResizable(false);
        secondaryStage.initOwner(primaryStage);
        secondaryStage.showAndWait();
    }

    /**
     * Adds an ingredient to a recipe.
     */
    @FXML
    private void ingredientAddButtonClicked() throws IOException {
        Recipe selectedRecipe = getSelectedRecipe();

        if (selectedRecipe == null) {
            System.out.println("No recipe selected!");
            return;
        }

        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        showAddRecipeIngredientWindow();

        RecipeIngredient newIngredient =
                addRecipeIngredientCtrl.getRecipeIngredient();

        if (newIngredient != null) {
            newIngredient.setRecipe(selectedRecipe);

            this.recipeServerUtils.createRecipeIngredient(newIngredient);

            selectedRecipe.getIngredients().add(newIngredient);

        }

        showIngredients(selectedRecipe);
    }

    private void addRecipeIngredientToUI(RecipeIngredient recipeIngredient) {
        // To create a new row
        HBox ingredientItem = new HBox();
        ingredientItem.setUserData(recipeIngredient.getId());
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
        removeButton.setUserData(recipeIngredient.getId()); // To be able to identify which recipeIngredient to delete
                                                            // when remove button is clicked

        // Put it all into HBox
        ingredientItem.getChildren().addAll(itemLabel, spacer, editButton, removeButton);

        // Put the HBox (the row) in the ingredientList
        ingredientList
                .getChildren()
                .add(ingredientItem);
    }

    /**
     * Removes an ingredient from a recipe.
     */
    @FXML
    private void ingredientRemoveButtonClicked(ActionEvent event) {
        Button removeButton = (Button) event.getSource(); // To identify the button of which ingredient was clicked
        HBox ingredientToRemove = (HBox) removeButton.getParent(); // To delete the whole row
        RecipeIngredientKey id = (RecipeIngredientKey) ingredientToRemove.getUserData();

        if(id == null) return;

        try{
            boolean success = this.recipeServerUtils.deleteRecipeIngredient(id.getRecipeId(),
                    id.getIngredientId());

            if (success) {
                ingredientList.getChildren().remove(ingredientToRemove);

                if(selectedRecipe != null) {
                    selectedRecipe.getIngredients().removeIf(ri ->
                            ri.getId().equals(id)
                    );
                }
            }

            System.out.println("Removed ingredient.");

        } catch (Exception e) {
            System.err.println("Error deleting recipe ingredient from Database");
        }




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
        RecipeIngredient updated = addRecipeIngredientCtrl.getRecipeIngredient();

        if(updated != null) {
            RecipeIngredientKey id = (RecipeIngredientKey) ingredientToEdit.getUserData();
            this.recipeServerUtils.updateRecipeIngredient(id.getRecipeId(), id.getIngredientId(), updated);

            List<RecipeIngredient> ingredients = selectedRecipe.getIngredients();
            for(int i = 0; i < ingredients.size(); i++) {
                if(ingredients.get(i).getId().equals(id)) {
                    updated.setRecipe(selectedRecipe);
                    ingredients.set(i, updated);
                    break;
                }
            }
        }


        showIngredients(selectedRecipe);

        System.out.println("Edited ingredient.");
    }

    // ALL INSTRUCTION / PREPARATION STEP METHODS BELOW HERE:

    /**
     * Displays the instructions of a recipe in the UI.
     *
     * @param recipe the recipe whose instructions are to be displayed
     */
    private void showInstructions(Recipe recipe) {
        preparationList.getChildren().clear();

        List<String> steps = recipe.getInstructions();

        for (int i = 0; i < steps.size(); i++) {
            HBox row = createExecutionStepRow(i, steps.get(i), false);
            preparationList.getChildren().add(row);
        }
    }

    /**
     * This is a placeholder method for further development Method prints out a
     * message when the button is clicked.
     */
    @FXML
    private void preparationAddButtonClicked() {
        if (selectedRecipe == null) {
            return;
        }

        // Add empty step to recipe
        selectedRecipe.getInstructions().add("");

        int newIndex = selectedRecipe.getInstructions().size() - 1;
        HBox row = createExecutionStepRow(newIndex, "", true); // editable
        preparationList.getChildren().add(row);
    }

    /**
     * Creates a row for a preparation step.
     *
     * @param index the index of the step
     * @param initialText the initial text of the step
     * @param startEditable whether the step is initially editable
     */
    private HBox createExecutionStepRow(int index, String initialText, boolean startEditable) {
        // Create a new HBox (a preparation step)
        // [ 1 ][ Label(text) ][ ✏ ][ ✖ ] when not editing
        // [ 1 ][ TextField ][ ✔ ][ ✖ ] when editing
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        //index
        Label indexLabel = new Label((index + 1) + ".");
        indexLabel.setMinWidth(30);

        Label textLabel = new Label(initialText);
        //textLabel.setPrefWidth(400);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        TextField textField = new TextField(initialText);
        //textField.setPrefWidth(400);
        textField.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(textLabel, Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Button editSaveButton = new Button();
        Button deleteButton = new Button("✖");


        final Runnable[] switchToEdit = new Runnable[1];
        final Runnable[] saveStep = new Runnable[1];

        saveStep[0] = () -> {
            String text = textField.getText().trim();
            if (text.isEmpty()) {
                return;
            }

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
     * This is a placeholder a helper method for further list management Method
     * prints out a message when the button is clicked
     *
     * @param ingredient the ingredient to be added
     */
    @FXML
    private void addIngredientToList(String ingredient) {
        System.out.println("Added ingredient: " + ingredient);
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
        nutritionalValueCtrl.initialize();
        secondaryStage.setTitle("Nutritional Values");
        secondaryStage.setScene(nutritionScene);
        secondaryStage.initOwner(primaryStage); // makes it modal to main window if desired
        secondaryStage.showAndWait();

        sidebarList.getSelectionModel().clearSelection();
    }

    /**
     * Makes sure the shopping list window is opened once the shopping list
     * button is pressed
     */
    @FXML
    void showShoppingListWindow() {
        if (shoppingListScene == null) {
            System.err.println("Shopping list scene not initialized!");
            return;
        }

        // Refresh the shopping list data from config before showing
        ShoppingListCtrl shoppingListCtrl = (ShoppingListCtrl) shoppingListScene.getUserData();
        if (shoppingListCtrl != null) {
            shoppingListCtrl.refreshFromConfig();
        }

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Shopping List");
        secondaryStage.setScene(shoppingListScene);
        secondaryStage.initOwner(primaryStage); // makes it modal to main window if desired
        secondaryStage.show();
    }

    /**
     * Makes sure the recipe shopping list window is opened once the
     * shopping list button is pressed
     */
    @FXML
    void showRecipeShoppingWindow() {
        if (recipeShoppingListScene == null || recipeShoppingListCtrl == null) {
            System.err.println("Recipe shopping list scene or controller not initialized!");
            return;
        }

        // Check if a recipe is selected
        if (selectedRecipe == null) {
            showError("Please select a recipe first");
            return;
        }

        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("Recipe Shopping List - " + selectedRecipe.getTitle());
        secondaryStage.setScene(recipeShoppingListScene);
        secondaryStage.initOwner(primaryStage); // makes it modal to main window if desired
        
        // Initialize the controller with the selected recipe
        recipeShoppingListCtrl.initializeWithRecipe(selectedRecipe);
        
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
     * Clone's the selected recipe, copying the name and adding (Clone) behind
     * it
     */
    @FXML
    void cloneRecipe() {

        Long currentRecipeId = selectedRecipe.getRecipeId();

        if (currentRecipeId == null) {
            System.out.println("No Recipe selected!");
            return;
        }

        try {
            Recipe original = recipeServerUtils.getRecipe(currentRecipeId);

            Recipe newRecipe = new Recipe(
                    original.getTitle() + " (Clone)",
                    original.getLanguage()
            );

            newRecipe.setServings(original.getServings());
            newRecipe.setInstructions(new ArrayList<>(original.getInstructions()));

            Long newId = recipeServerUtils.createRecipe(newRecipe);
            newRecipe.setRecipeId(newId);

            for(RecipeIngredient oldI : original.getIngredients()) {
                RecipeIngredient newI;

                if(oldI.isFormal()){
                    newI = RecipeIngredient.formal(newRecipe, oldI.getIngredient(), oldI.getAmount(),  oldI.getUnit());
                }else{
                    newI = RecipeIngredient.informal(newRecipe, oldI.getIngredient(), oldI.getInformal());
                }

                recipeServerUtils.createRecipeIngredient(newI);

                newRecipe.getIngredients().add(newI);

            }

            recipeCollection.add(newRecipe);

            System.out.println("It worked! Cloned recipe ID: " + newId);

        } catch (Exception e) {
            System.err.println("Clone failed: " + e.getMessage());
        }

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
        editTitleStage.setResizable(false);

        editTitleStage.show();
    }

    /**
     * Returns selected recipe.
     *
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

    /**
     * Shows an error message dialog.
     * 
     * @param message error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateTitleLabel(String title) {
        titleRecipeLabel.setText(title);
    }

    @FXML
    private ListView<Recipe> sidebarList;

    @FXML
    private Button favoriteS;

    @FXML
    private Button addIngredientButton;

    @FXML
    private Button shoppingListButton;

    @FXML
    private Button addRecipeButton;

    @FXML
    private Button recipeShoppingButton;

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
    private Label titleRecipeLabel;

    @FXML
    private Button downloadButton;
    @FXML
    private ScrollPane prepScrollPane;

    @FXML
    private VBox preparationList;

    @FXML
    private Button addStepButton;

    @FXML
    private VBox noRecipePane;

    @FXML
    private VBox allRecipePane;

    @FXML
    private void downloadButtonClicked() throws IOException {
        Recipe selectedRecipe = (Recipe) sidebarList.getSelectionModel().getSelectedItem();
        if (selectedRecipe == null) {
            System.out.println("Please select a recipe.");
            return;
        }

        saveRecipe(selectedRecipe);
    }

    /**
     * Saves a recipe in chosen path.
     *
     * @param recipe recipe to be saved
     */
    private void saveRecipe(Recipe recipe) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save recipe");
        fileChooser.setInitialFileName(recipe.getTitle() + ".txt");

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                String text = (new RecipeFormatter(recipe)).formatPlaintext();
                FileUtils.writeStringToFile(file, text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
