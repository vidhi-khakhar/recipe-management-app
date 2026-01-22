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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.controlsfx.control.CheckComboBox;

import com.google.inject.Inject;

import client.RecipeFormatter;
import client.config.ConfigService;
import client.favorite.FavoriteService;
import client.favorite.RecipeSortingService;
import client.scenes.state.EmptyStateReason;
import client.search.RecipeCollection;
import client.search.SearchService;
import client.utils.RecipeServerUtils;
import commons.Recipe;
import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
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
    private AddButtonCtrl addButtonCtrl;
    private EditTitleCtrl editTitleCtrl;

    private final RecipeServerUtils recipeServerUtils;
    private final FavoriteService favoriteService;
    private final ObservableList<Recipe> recipesObservableList = FXCollections.observableArrayList();
    private Recipe selectedRecipe;
    private final SearchService searchService;
    private final RecipeSortingService recipeSortingService;
    private final RecipeCollection recipeCollection;
    private final List<String> filteredLanguages = new ArrayList<>();

    private final ConfigService configService;

    @FXML
    private Spinner<Integer> scaler;

    @FXML
    private ListView<Recipe> sidebarList;

    @FXML
    private Button favoritesOnly;

    @FXML
    private Button cloneRecipeButton;

    @FXML
    private Label noRecipeTitle;

    @FXML
    private Label noRecipeSubtitle;

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
    private Label sortLabel;

    @FXML
    private Label FoodPalLabel;

    @FXML
    private Button refreshButton;

    @FXML
    private Button removeItemButton;

    @FXML
    private Button removeRecipeButton;

    @FXML
    private Pane healthStatus;

    @FXML
    private Label healthyStatus;

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
    private Label servingsValueLabel;

    @FXML
    private Label inferredKcalsLabel;

    @FXML
    private MenuButton languageSetter;

    @FXML
    private CheckComboBox<String> languageCheckBox;

    @FXML
    private Label recipeLanguageLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label ingredientsLabel;

    @FXML
    private Label instructionsLabel;

    @FXML
    private Label nutritionalValuesLabel;

    @FXML
    private Label servingsLabel;
    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private Button sortButton;

    /**
     * Initializes the scenes
     *
     * @param primaryStage the main stage
     * @param nutritionScene the nutrition scene
     * @param recipeAddScene the add recipe scene
     * @param addRecipeIngredientScene the add ingredient to recipe scene
     * @param addRecipeIngredientCtrl the add ingredient to recipe controller
     * @param nutritionalValueCtrl the nutritional value controller
     * @param recipeShoppingListCtrl the recipe shopping list controller
     */
    public void initScenes(Stage primaryStage, Scene nutritionScene,
            Scene recipeAddScene, Scene addRecipeIngredientScene,
            AddRecipeIngredientCtrl addRecipeIngredientCtrl,
            Scene editTitleScene, Scene shoppingListScene, Scene recipeShoppingListScene,
            NutritionalValueCtrl nutritionalValueCtrl,
            RecipeShoppingListCtrl recipeShoppingListCtrl,
            AddButtonCtrl addButtonCtrl, EditTitleCtrl editTitleCtrl) {

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
        this.addButtonCtrl = addButtonCtrl;
        this.editTitleCtrl = editTitleCtrl;
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
            FavoriteService favoriteService, RecipeCollection recipeCollection,
                    ConfigService configService, RecipeSortingService recipeSortingService) {
        this.recipeServerUtils = recipeServerUtils;
        this.searchService = searchService;
        this.favoriteService = favoriteService;
        this.recipeCollection = recipeCollection;
        this.configService = configService;
        this.recipeSortingService = recipeSortingService;
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
        ResourceBundle props = getTranslationsBundle();
        // Set initial flag based on saved config
        String currentLang = configService.getLanguage();
        switch (currentLang) {
            case "en" ->
                setEnglishLanguage();
            case "nl" ->
                setDutchLanguage();
            case "ro" ->
                setRomanianLanguage();
            case "es" ->
                setSpanishLanguage();
            default ->
                setEnglishLanguage();
        }

        languageCheckBox.getItems().addAll("English", "Dutch", "Romanian", "Spanish");

        languageCheckBox.getCheckModel().getCheckedItems().addListener(this::filterLanguages);

        sidebarList.setItems(recipesObservableList);

        sidebarList.setCellFactory(lv
                -> new RecipeListCell(favoriteService)
        );

        sidebarList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldRecipe, newRecipe) -> {
                    if (newRecipe != null) {
                        onRecipeSelected(newRecipe);
                        baseServings = newRecipe.getServings();
                        servingsValueLabel.setText(String.valueOf(baseServings));
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

        ObservableList<String> sortItems =  FXCollections.observableArrayList("A-Z", props.getString("label.newest"));
        sortComboBox.setItems(sortItems);
        sortComboBox.getSelectionModel().select(0);

        sortComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                applySearch();
            }
        });

        loadRecipesFromDatabase();
        scaler.setPromptText("scale");
        scaler.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50));
        scaler.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(oldValue)) {
                scaleRecipe(getSelectedRecipe());
            }
        });

    }

    @FXML
    private void setEnglishLanguage() {
        setAppLanguage("en", "/images/EnglishFlag.png");
    }

    @FXML
    private void setDutchLanguage() {
        setAppLanguage("nl", "/images/DutchFlag.png");
    }

    @FXML
    private void setRomanianLanguage() {
        setAppLanguage("ro", "/images/RomanianFlag.png");
    }

    @FXML
    private void setSpanishLanguage() {
        setAppLanguage("es", "/images/SpanishFlag.png");
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
            if (!removedIDs.isEmpty()) {
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
            } else {
                sidebarList.getSelectionModel().clearSelection();
            }

        } catch (Exception e) {
            System.out.println("Failed to load recipes: " + e.getMessage());
        }
    }

    /**
     * The alert to let the user know their favorite recipe got deleted by
     * someone else.
     *
     * @param removedIds the removed recipes' IDs
     */
    private void showFavoriteRemovedAlert(List<Long> removedIds) {
        java.util.ResourceBundle props = getTranslationsBundle();

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(props.getString("alert.favouriteDeleted"));
        alert.setHeaderText(props.getString("alert.favouriteDeleted.header"));
        alert.setContentText(props.getString("alert.favouriteDeleted.content"));

        alert.showAndWait();
    }

    /**
     * Updates the recipe stack pane based on whether there are recipes in the
     * sidebar list.
     */
    @FXML
    public void updateRecipeStackPane(EmptyStateReason reason) {
        boolean hasRecipes = !sidebarList.getItems().isEmpty();

        if (hasRecipes) {
            noRecipePane.setVisible(false);
            allRecipePane.setVisible(true);
            return;
        }

        noRecipePane.setVisible(true);
        allRecipePane.setVisible(false);

        if (reason == null) {
            return;
        }

        java.util.ResourceBundle props = getTranslationsBundle();

        switch (reason) {
            case NO_RECIPES_EXIST -> {
                noRecipeTitle.setText(props.getString("label.noRecipeTitle"));
                noRecipeSubtitle.setText(props.getString("label.noRecipeSubtitle"));
            }
            case NO_SEARCH_RESULTS -> {
                noRecipeTitle.setText(props.getString("label.noRecipesFound"));
                noRecipeSubtitle.setText(props.getString("label.noRecipesFoundSubtitle"));
            }
            case NO_FAVORITES -> {
                noRecipeTitle.setText(props.getString("label.noFavoritesFound"));
                noRecipeSubtitle.setText(props.getString("label.noFavoritesFoundSubtitle"));
            }
        }
    }


    /**
     * Changes the UI according to the selected recipe
     *
     * @param recipe the recipe that is selected
     */
    private void onRecipeSelected(Recipe recipe) {
        try {
            recipeServerUtils.getRecipe(recipe.getRecipeId());

            initScalingValues(recipe);

            if (this.selectedRecipe != null && this.selectedRecipe != recipe) {
                restoreBaseValues(this.selectedRecipe);
            }

            this.selectedRecipe = recipe;
            String recipeLanguage = recipe.getLanguage();
            updateRecipeLanguageLabel(recipeLanguage);
            titleRecipeLabel.setText(recipe.getTitle());

            showInstructions(recipe);
            showIngredients(recipe);
            showNutritionalValues(recipe);
            setHealthStatus(recipe);
        } catch (Exception e) {
            java.util.ResourceBundle props = getTranslationsBundle();
            System.out.println(e.getMessage());
            recoverFromError(props.getString("alert.recipeMissing"));
        }
    }

    /**
     * Shows an alert that tells the user something went wrong and the UI will
     * get refreshed. After that, the UI will get refreshed.
     *
     * @param userMessage The specific error message passed down by the
     * developer.
     */
    public void recoverFromError(String userMessage) {
        java.util.ResourceBundle props = getTranslationsBundle();

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(props.getString("alert.somethingWentWrong"));
        alert.setHeaderText(userMessage);
        alert.setContentText(props.getString("alert.recipeMissing.content"));
        alert.showAndWait();

        reloadApplicationState();
    }

    /**
     * Reloads the application, this is different from the refresh UI button as
     * this only does the part needed for the error handling.
     */
    private void reloadApplicationState() {
        try {
            loadRecipesFromDatabase();
            favoriteService.loadFromConfig();
            sidebarList.refresh();

            updateRecipeStackPane(
                    recipesObservableList.isEmpty()
                    ? EmptyStateReason.NO_RECIPES_EXIST
                    : null
            );

        } catch (Exception e) {
            java.util.ResourceBundle props = getTranslationsBundle();
            showError(props.getString("alert.reloadFailed"));
        }
    }

    /**
     * Adds the recipe to the list that is displayed by the UI.
     *
     * @param recipe the to be added recipe.
     */
    public void addRecipe(Recipe recipe) {
        recipeCollection.add(recipe);
        applySearch();
        sidebarList.getSelectionModel().select(recipe);
        updateRecipeStackPane(null);
    }

    /**
     * Determines which recipe is selected and passes it to the helper methods.
     */
    @FXML
    private void recipeRemoveButtonClicked() {
        Recipe selected = sidebarList.getSelectionModel().getSelectedItem();

        requireSelectedRecipe();

        if (!deleteConfirmation()) {
            return;
        }

        removeRecipe(selected);
        // If the selected recipe is a favorite, the favorite id gets removed from config
        if (favoriteService.isFavorite(selected)) {
            favoriteService.removeFavorite(selected);
        }

        updateRecipeStackPane(null);

        if (!recipesObservableList.isEmpty()) {
            sidebarList.getSelectionModel().selectFirst();
        }
    }

    /**
     * Opens an alert when user clicks the recipe deletion button.
     *
     * @return true if user confirms the deletion false if user cancels the
     * deletion
     */
    private boolean deleteConfirmation() {
        java.util.ResourceBundle props = getTranslationsBundle();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(props.getString("alert.confirmationText"));
        alert.setHeaderText(props.getString("alert.confirmDelete"));

        if (favoriteService.isFavorite(selectedRecipe)) {
            alert.setContentText(props.getString("alert.favoriteNotice"));
        }

        // Wait for the confirmation from the user
        Optional<ButtonType> result = alert.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;
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
        Recipe previouslySelected = getSelectedRecipe();
        java.util.ResourceBundle props = getTranslationsBundle();

        String query = searchBar.getText();
        boolean searchFavorites = props.getString("label.displayFavouriteRecipesToggleOn")
                .equals(favoritesOnly.getText());

        // 1. Fetch and Filter
        List<Recipe> fromServer = searchService.search(query, searchFavorites);

        List<Recipe> filtered = fromServer.stream()
                .filter(recipe -> filteredLanguages.isEmpty() ||
                        filteredLanguages.contains(recipe.getLanguage().toLowerCase().trim()))
                .collect(Collectors.toList());

        // 2. Sort
        String sortType = sortComboBox.getValue();
        if ("A-Z".equals(sortType)) {
            filtered = recipeSortingService.sortByTitleAsc(filtered);
        } else {
            filtered = recipeSortingService.sortByDateDesc(filtered);
        }

        if ("↑☰".equals(sortButton.getText())) {
            filtered = filtered.reversed();
        }

        // 3. Update List
        recipesObservableList.setAll(filtered);

        // 4. Handle UI State (Extracted to reduce CC)
        updateSearchInterfaceState(filtered, query, searchFavorites, previouslySelected);
    }

    private void updateSearchInterfaceState(List<Recipe> filtered, String query,
                                            boolean searchFavorites, Recipe previouslySelected) {
        // Handle Empty States
        if (filtered.isEmpty()) {
            if (searchFavorites) {
                updateRecipeStackPane(EmptyStateReason.NO_FAVORITES);
            } else if (query.trim().isEmpty()) {
                updateRecipeStackPane(EmptyStateReason.NO_RECIPES_EXIST);
            } else {
                updateRecipeStackPane(EmptyStateReason.NO_SEARCH_RESULTS);
            }
            return; // Guard clause prevents unnecessary checking below
        }

        // Handle Selection State (List is not empty)
        updateRecipeStackPane(null);

        if (previouslySelected != null && filtered.contains(previouslySelected)) {
            sidebarList.getSelectionModel().select(previouslySelected);
        } else {
            sidebarList.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void changeSort(){
        if(sortButton.getText().equals("↑☰")){
            sortButton.setText("↓☰");
        }else{
            sortButton.setText("↑☰");
        }
        applySearch();
    }


    @FXML
    private void searchFavorites() {
        java.util.ResourceBundle props = getTranslationsBundle();

        if (favoritesOnly.getText().equals(props.getString("label.displayFavouriteRecipesToggleOn"))) {
            favoritesOnly.setText(props.getString("label.displayFavouriteRecipesToggleOff"));
        } else {
            favoritesOnly.setText(props.getString("label.displayFavouriteRecipesToggleOn"));
        }

        applySearch();
    }

    /**
     * Refreshes the recipe list by loading all recipes from the database
     */
    @FXML
    private void refreshButtonClicked() {
        try {
            configService.reload();
            reloadApplicationState();

            Recipe selected = sidebarList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showIngredients(selected);
            }

        } catch (Exception e) {
            showError("alert.refreshFailed");
        }
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

        showNutritionalValues(recipe);
        setHealthStatus(recipe);
    }

    /**
     * Opens the add ingredient to recipe popup window.
     */
    @FXML
    void showAddRecipeIngredientWindow(boolean isAdd) {
        if (addRecipeIngredientScene == null) {
            System.err.println("AddRecipeIngredientScene not initialized!");
            return;
        }

        java.util.ResourceBundle props = getTranslationsBundle();

        Stage secondaryStage = new Stage();
        addRecipeIngredientCtrl.onStart(isAdd);
        addRecipeIngredientCtrl.initScenes(nutritionScene, addRecipeIngredientScene);

        secondaryStage.setTitle(props.getString("label.addRecipeIngredientsTitle"));

        secondaryStage.setScene(addRecipeIngredientScene);
        secondaryStage.setResizable(false);
        secondaryStage.initOwner(primaryStage);
        secondaryStage.showAndWait();
    }


    /**
     * Adds an ingredient to a recipe.
     */
    @FXML
    private void ingredientAddButtonClicked() {
        java.util.ResourceBundle props = getTranslationsBundle();
        try {
            Recipe selectedRecipe = getSelectedRecipe();

            requireSelectedRecipe();

            addRecipeIngredientCtrl.setRecipe(selectedRecipe);
            showAddRecipeIngredientWindow(true);

            RecipeIngredient newIngredient = addRecipeIngredientCtrl.getRecipeIngredient();

            if (newIngredient != null) {
                newIngredient.setRecipe(selectedRecipe);

                this.recipeServerUtils.createRecipeIngredient(newIngredient);

                selectedRecipe.getIngredients().add(newIngredient);

                if (newIngredient.isFormal()) {
                    baseAmounts.put(newIngredient, newIngredient.getAmount());
                    baseUnits.put(newIngredient, newIngredient.getUnit());
                }
            }

            showIngredients(selectedRecipe);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            recoverFromError(props.getString("alert.recipeIngredientFail"));
        }
    }

    private void addRecipeIngredientToUI(RecipeIngredient recipeIngredient) {
        java.util.ResourceBundle props = getTranslationsBundle();

        // Create a new row
        HBox ingredientItem = new HBox();
        ingredientItem.setUserData(recipeIngredient.getId());
        ingredientItem.setAlignment(Pos.CENTER_LEFT);
        ingredientItem.setSpacing(8.0);
        ingredientItem.getStyleClass().add("recipe-item");

        Label itemLabel = new Label(recipeIngredient.toString());
        itemLabel.setStyle("-fx-padding: 0 0 0 9;"); // <Insets left="9.0">

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Edit button
        Button editButton = new Button(props.getString("label.editRecipeButton"));
        editButton.setOnAction(event -> {
            try {
                editIngredientButtonClicked(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Remove button
        Button removeButton = new Button(props.getString("label.removeIngredientButton"));
        removeButton.setOnAction(this::ingredientRemoveButtonClicked);
        removeButton.setUserData(recipeIngredient.getId());

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
        selectedRecipe = getSelectedRecipe();
        requireSelectedRecipe();

        Button removeButton = (Button) event.getSource(); // To identify the button of which ingredient was clicked
        HBox ingredientToRemove = (HBox) removeButton.getParent(); // To delete the whole row
        RecipeIngredientKey id = (RecipeIngredientKey) ingredientToRemove.getUserData();

        if (id == null) {
            return;
        }

        try {
            boolean success = this.recipeServerUtils.deleteRecipeIngredient(id.getRecipeId(),
                    id.getIngredientId());

            if (success) {
                ingredientList.getChildren().remove(ingredientToRemove);

                if (selectedRecipe != null) {
                    selectedRecipe.getIngredients().removeIf(ri
                            -> ri.getId().equals(id)
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

        RecipeIngredientKey id = (RecipeIngredientKey) ingredientToEdit.getUserData();
        addRecipeIngredientCtrl.setRecipe(selectedRecipe);
        addRecipeIngredientCtrl.setIngredient(id.getIngredientId());
        showAddRecipeIngredientWindow(false);
        RecipeIngredient updated = addRecipeIngredientCtrl.getRecipeIngredient();

        if (updated != null) {
            baseAmounts.put(updated, updated.getAmount());
            baseUnits.put(updated, updated.getUnit());
            this.recipeServerUtils.updateRecipeIngredient(id.getRecipeId(), id.getIngredientId(), updated);

            List<RecipeIngredient> ingredients = selectedRecipe.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                if (ingredients.get(i).getId().equals(id)) {
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
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        Label indexLabel = new Label((index + 1) + ".");
        indexLabel.setMinWidth(30);

        Label textLabel = new Label(initialText);
        textLabel.setMaxWidth(Double.MAX_VALUE);

        TextField textField = new TextField(initialText);
        textField.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(textLabel, Priority.ALWAYS);
        HBox.setHgrow(textField, Priority.ALWAYS);

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
                System.out.println(e.getMessage());
                recoverFromError(getTranslationsBundle().getString("alert.saveInstructionsFail"));
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

        // keep this exactly as before to ensure deletion works
        deleteButton.setOnAction(e -> handleDeleteStep(index));

        return row;
    }

    private void handleDeleteStep(int index) {
        ResourceBundle props = getTranslationsBundle();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(props.getString("alert.deleteStep"));
        alert.setHeaderText(props.getString("alert.confirmDeleteStep"));

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
        nutritionalValueCtrl.initialize();
        secondaryStage.setTitle(props.getString("label.nutritionalValuesWindow"));
        secondaryStage.setScene(nutritionScene);
        secondaryStage.initOwner(primaryStage);
        secondaryStage.setResizable(false);
        secondaryStage.showAndWait();
    }

    /**
     * Makes sure the shopping list window is opened once the shopping list
     * button is pressed
     */
    @FXML
    void showShoppingListWindow() {
        ResourceBundle props = getTranslationsBundle();
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
        secondaryStage.setTitle(props.getString("label.shoppingListWindow"));
        secondaryStage.setScene(shoppingListScene);
        secondaryStage.initOwner(primaryStage); // makes it modal to main window if desired
        secondaryStage.show();
    }

    /**
     * Makes sure the recipe shopping list window is opened once the shopping
     * list button is pressed
     */
    @FXML
    void showRecipeShoppingWindow() {
        ResourceBundle props = getTranslationsBundle();
        if (recipeShoppingListScene == null || recipeShoppingListCtrl == null) {
            System.err.println("Recipe shopping list scene or controller not initialized!");
            return;
        }

        requireSelectedRecipe();
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle(props.getString("label.shoppingListTitle") + selectedRecipe.getTitle());
        secondaryStage.setScene(recipeShoppingListScene);
        secondaryStage.initOwner(primaryStage);

        recipeShoppingListCtrl.loadLanguages();
        recipeShoppingListCtrl.initializeWithRecipe(selectedRecipe);


        secondaryStage.show();
    }


    /**
     * Makes sure the window with the add recipe functionality is opened once
     * the Add button is pressed.
     */
    @FXML
    void showAddRecipeWindow() {
        ResourceBundle props = getTranslationsBundle();

        if (recipeAddScene == null) {
            System.err.println("Recipe add scene not initialized!");
            return;
        }

        Stage recipeAddStage = new Stage();
        recipeAddStage.setTitle(props.getString("label.addRecipeWindowTitle"));
        recipeAddStage.setScene(recipeAddScene);
        recipeAddStage.initOwner(primaryStage);
        recipeAddStage.setResizable(false);
        addButtonCtrl.onShow();
        recipeAddStage.show();
    }

    /**
     * Clone's the selected recipe, copying the name and adding (Clone) behind
     * it
     */
    @FXML
    void cloneRecipe() {

        Long currentRecipeId = selectedRecipe.getRecipeId();

        ResourceBundle props = getTranslationsBundle();

        requireSelectedRecipe();

        try {
            Recipe original = recipeServerUtils.getRecipe(currentRecipeId);

            String clonedTitle = generateCloneTitle(original.getTitle());

            Recipe newRecipe = new Recipe(
                    clonedTitle,
                    original.getLanguage()
            );

            newRecipe.setServings(original.getServings());
            newRecipe.setInstructions(new ArrayList<>(original.getInstructions()));

            Long newId = recipeServerUtils.createRecipe(newRecipe);
            newRecipe.setRecipeId(newId);

            for (RecipeIngredient oldI : original.getIngredients()) {
                RecipeIngredient newI;

                if (oldI.isFormal()) {
                    newI = RecipeIngredient.formal(newRecipe, oldI.getIngredient(), oldI.getAmount(), oldI.getUnit());
                } else {
                    newI = RecipeIngredient.informal(newRecipe, oldI.getIngredient(), oldI.getInformal());
                }

                recipeServerUtils.createRecipeIngredient(newI);

                newRecipe.getIngredients().add(newI);

            }

            recipeCollection.add(newRecipe);
            applySearch();

            System.out.println("It worked! Cloned recipe ID: " + newId);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            recoverFromError(props.getString("alert.cloneFailed"));
        }
    }

    /**
     * Generates a new title for a cloned recipe based on the original title.
     * Checks existing recipes so that clone numbers increment correctly.
     *
     * @param originalTitle the title of the original recipe
     * @return the generated title for the cloned recipe
     */
    private String generateCloneTitle(String originalTitle) {
        ResourceBundle props = getTranslationsBundle();

        String cloneWord = props.getString("label.cloneFunctionality");

        // Extract base title
        var pattern = java.util.regex.Pattern.compile(
                "^(.*?)(?:\\s*\\(" + java.util.regex.Pattern.quote(cloneWord)
                        + "(?:\\s*(\\d+))?\\))?$"
        );
        var matcher = pattern.matcher(originalTitle.trim());

        String baseTitle = originalTitle;
        if (matcher.matches()) {
            baseTitle = matcher.group(1).trim();
        }

        int maxCloneNumber = 0;

        // Loop through all existing recipes to find the highest clone number
        for (Recipe r : recipesObservableList) {
            String title = r.getTitle().trim();
            var m = pattern.matcher(title);
            if (m.matches()) {
                String candidateBase = m.group(1).trim();
                String numGroup = m.group(2);

                if (!candidateBase.equals(baseTitle)) {
                    continue;
                }

                int num = 0;
                if (numGroup != null) {
                    num = Integer.parseInt(numGroup);
                } else if (title.endsWith("(" + cloneWord + ")")) {
                    num = 1;
                }

                if (num > maxCloneNumber) {
                    maxCloneNumber = num;
                }
            }
        }

        int nextNumber = maxCloneNumber + 1;
        return baseTitle + " (" + cloneWord + " " + nextNumber + ")";
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
        ResourceBundle props = getTranslationsBundle();

        if (editTitleScene == null) {
            System.err.println("Scene not initialized!");
            return;
        }

        Stage editTitleStage = new Stage();
        editTitleStage.setTitle(props.getString("label.editRecipeDetails"));
        editTitleStage.setScene(editTitleScene);
        editTitleStage.initOwner(primaryStage);
        editTitleStage.setResizable(false);
        editTitleCtrl.loadLanguages();
        editTitleStage.show();
    }

    public void refreshSidebarList() {
        sidebarList.refresh();
    }

    /**
     * Shows an error message dialog.
     * @param messageKey
     */
    private void showError(String messageKey) {
        ResourceBundle props = getTranslationsBundle();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(props.getString("alert.error"));
        alert.setHeaderText(null);
        alert.setContentText(props.getString(messageKey));
        alert.showAndWait();
    }


    public void updateTitleLabel(String title) {
        titleRecipeLabel.setText(title);
    }

    /**
     * changing the Recipe language
     *
     * @param recipeLanguage
     */
    public void updateRecipeLanguageLabel(String recipeLanguage) {
        ResourceBundle props = getTranslationsBundle();
        String fullLanguage = RecipeFormatter.getFullRecipeLanguage(recipeLanguage, props);
        recipeLanguageLabel.setText(fullLanguage);
    }

    @FXML
    private void downloadButtonClicked() throws IOException {
        ResourceBundle props = getTranslationsBundle();
        requireSelectedRecipe();


        saveRecipe(selectedRecipe);
    }


     /**
     * Helper method to update the language both visually and in config.
     *
     * @param languageCode the ISO code of the language ("en", "nl", "ro", "es")
     * @param imagePath the path to the flag image resource
     */
    private void setAppLanguage(String languageCode, String imagePath) {
        System.out.println("Switching language to: " + languageCode);
        configService.setLanguage(languageCode);

        // Update MenuButton graphic (flags)
        var stream = getClass().getResourceAsStream(imagePath);
        if (stream != null) {
            ImageView flag = new ImageView(new Image(stream));
            flag.setFitWidth(20);
            flag.setFitHeight(20);
            languageSetter.setGraphic(flag);
            languageSetter.setText("");
        } else {
            languageSetter.setGraphic(null);
            languageSetter.setText(languageCode.toUpperCase());
        }

        initializeTextProperties();

        Recipe selected = getSelectedRecipe();
        if (selected != null) {
            updateRecipeLanguageLabel(selected.getLanguage());  // Use the recipe's language, not the UI language
        }
    }

    /**
     * A method that sets the buttonIcon that displays the language that the app
     * is working in
     *
     * @param path the path to the image that will be displayed
     */
    private void updateLanguageButton(String path) {
        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));

        ImageView buttonIcon = new ImageView(image);
        buttonIcon.setFitWidth(20);
        buttonIcon.setFitHeight(20);

        languageSetter.setGraphic(buttonIcon);
        languageSetter.setText(""); // this line is neccesary bc otherwise you still get ... next to the flag.

    }

    /** Binds all the UI text properties to the current ResourceBundle
     * Call  this whenever the language changes
     */
    @FXML
    private void initializeTextProperties() {
        ResourceBundle props = getTranslationsBundle();
        updateStaticUILabels(props);
        updateSelectedRecipeLabels(props);
    }

    /* Sets to langauge */
    private void updateStaticUILabels(ResourceBundle props) {
        FoodPalLabel.setText(props.getString("label.title"));
        searchBar.setPromptText(props.getString("label.searchPromptText"));
        favoritesOnly.setText(props.getString("label.displayFavouriteRecipesToggleOff"));
        shoppingListButton.setText(props.getString("label.shoppingListWindow"));
        nutritionalValueButton.setText(props.getString("label.nutritionalValuesWindow"));
        addRecipeButton.setText(props.getString("label.addRecipeButton"));
        editRecipeTitleButton.setText(props.getString("label.editRecipeButton"));
        cloneRecipeButton.setText(props.getString("label.cloneRecipeButton"));
        removeItemButton.setText(props.getString("label.removeIngredientButton"));
        recipeShoppingButton.setText(props.getString("label.shopIngredientButton"));
        languageLabel.setText(props.getString("label.languageLabel"));
        ingredientsLabel.setText(props.getString("label.ingredientsLabel"));
        instructionsLabel.setText(props.getString("label.instructionsLabel"));
        nutritionalValuesLabel.setText(props.getString("label.nutritionalValuesLabel"));
        servingsLabel.setText(props.getString("label.servingsLabel"));
        downloadButton.setText(props.getString("label.downloadButton"));
        noRecipeTitle.setText(props.getString("label.noRecipeTitle"));
        noRecipeSubtitle.setText(props.getString("label.noRecipeSubtitle"));
        sortLabel.setText(props.getString("label.sort"));
    }

    private void updateSelectedRecipeLabels(ResourceBundle props) {
        Recipe selected = getSelectedRecipe();
        if (selected != null) {
            recipeLanguageLabel.setText(RecipeFormatter.getFullRecipeLanguage(selected.getLanguage(), props));
        }
    }


    /**
     * Returns the ResourceBundle for the currently selected language. This
     * ensures a consistent path to the translations file.
     */
    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return java.util.ResourceBundle.getBundle(
                    "client.scenes.Translations", // matches Translations_en.properties location
                    new java.util.Locale(languageCode)
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to load language bundle for code: " + configService.getLanguage());
            return java.util.ResourceBundle.getBundle("client.scenes.Translations", java.util.Locale.ENGLISH);
        }
    }

    /**
     * Saves a recipe in chosen path.
     *
     * @param recipe recipe to be saved
     */
    private void saveRecipe(Recipe recipe) throws IOException {
        ResourceBundle props = getTranslationsBundle();

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

    private int baseServings;
    private final Map<RecipeIngredient, Double> baseAmounts = new HashMap<>();
    private final Map<RecipeIngredient, Unit> baseUnits = new HashMap<>();

    private void initScalingValues(Recipe recipe) {

        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri.getAmount() != null && ri.getUnit() != null) {
                baseAmounts.put(ri, ri.getAmount());
                baseUnits.put(ri, ri.getUnit());
            }
        }
    }

    private double calculateKcal(Recipe recipe) {
        List<RecipeIngredient> ingredients = recipe.getIngredients().stream()
                .filter(ri -> ri.isFormal()).toList();

        double kcals = 0;
        double weight = 0;
        for (RecipeIngredient ingredient : ingredients) {
            kcals += ingredient.getIngredient().getKcalPer100g() * baseAmounts.get(ingredient) / 100;
            weight += baseAmounts.get(ingredient);
        }

        if (ingredients.isEmpty())
            return 0;

        return kcals / weight * 100;
    }


    private void showNutritionalValues(Recipe recipe) {
        inferredKcalsLabel.setText(String.valueOf(calculateKcal(recipe)));
        servingsValueLabel.setText(String.valueOf(recipe.getServings()));
    }

    private void scaleRecipe(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        int factor = 1;
        if (scaler.getValue() != null) {
            factor = scaler.getValue();
        }

        recipe.setServings(baseServings * factor);

        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (ri.isFormal()) {
                ri.setFormal(baseAmounts.get(ri) * factor, baseUnits.get(ri));
            }
        }

        inferredKcalsLabel.setText(String.valueOf(calculateKcal(recipe)));
        servingsValueLabel.setText(String.valueOf(baseServings * factor));
        normalizeUnits(recipe);
        showNutritionalValues(recipe);
        showIngredients(recipe);
    }


    private void normalizeUnits(Recipe recipe) {
        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (!ri.isFormal()) continue;

            if (ri.getUnit() == Unit.G && ri.getAmount() >= 1000) {
                ri.setFormal(ri.getAmount() / 1000, Unit.KG);
            } else if (ri.getUnit() == Unit.ML && ri.getAmount() >= 1000) {
                ri.setFormal(ri.getAmount() / 1000, Unit.L);
            }
        }
    }

    private void restoreBaseValues(Recipe recipe) {
        recipe.setServings(baseServings);

        for (RecipeIngredient ri : recipe.getIngredients()) {
            Double baseAmount = baseAmounts.get(ri);
            Unit baseUnit = baseUnits.get(ri);

            if (baseAmount != null && baseUnit != null) {
                ri.setFormal(baseAmount, baseUnit);
            }
        }
    }

    /**
     * Ensures a recipe is selected; shows an error if not.
     * @return true if a recipe is selected, false otherwise
     */
    private boolean requireSelectedRecipe() {
        ResourceBundle props = getTranslationsBundle();
        if (getSelectedRecipe() == null) {
            showError(props.getString("alert.selectARecipe"));
            return false;
        }
        return true;
    }

    /**
     * filter the languages
     * @param change the change from the languagecheckbox
     */
    @FXML
    private void filterLanguages(ListChangeListener.Change<? extends String> change) {
        boolean added;
        String language = "";
        String changed = change.toString();


        added = changed.contains("addition");

        if (changed.contains("English")) {
            language = "en";
        } else if (changed.contains("Dutch")) {
            language = "nl";
        } else if (changed.contains("Romanian")) {
            language = "ro";
        } else if (changed.contains("Spanish")) {
            language = "es";
        }

        if (added) {
            filteredLanguages.add(language);
        } else {
            filteredLanguages.remove(language);
        }

        configService.getConfig().setFilteredLanguages(new ArrayList<>(filteredLanguages));
        configService.save();
        applySearch();
    }



    private void setHealthStatus(Recipe recipe) {
        ResourceBundle props = getTranslationsBundle();
        double kcals = calculateKcal(recipe);

        if (kcals < 200) {
            healthStatus.setStyle("-fx-background-color: green");
            healthyStatus.setText(props.getString("label.healthyRecipe"));
            healthyStatus.setStyle("-fx-text-fill: #ffffff");
        } else if (kcals < 500) {
            healthStatus.setStyle("-fx-background-color: yellow");
            healthyStatus.setText(props.getString("label.UmHealthy"));
            healthyStatus.setStyle("-fx-text-fill: #000000");
        } else {
            healthyStatus.setText(props.getString("label.UnHealthy"));
            healthyStatus.setStyle("-fx-text-fill: #ffffff");
            healthStatus.setStyle("-fx-background-color: red");
        }
    }


    @FXML
    private void onInfoHealthButtonClicked() {
        ResourceBundle props = getTranslationsBundle();

        VBox root = new VBox();
        Label text = new Label(props.getString("label.healthyInfo"));
        text.setStyle("-fx-text-fill: #ffffff");

        root.getChildren().add(text);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #8191b3");

        Stage popup = new Stage();
        popup.setTitle(props.getString("label.healthInfo"));
        popup.setResizable(false);
        popup.setScene(new Scene(root, 400, 200));
        popup.show();
    }

}
