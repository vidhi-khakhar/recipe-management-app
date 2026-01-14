package client.scenes;

import client.utils.IngredientServerUtils;
import client.utils.RecipeServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Recipe;
import commons.RecipeIngredient;
import jakarta.ws.rs.ProcessingException;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class NutritionalValueCtrl {

    private final IngredientServerUtils ingredientServerUtils;
    private final RecipeServerUtils recipeServerUtils;

    @Inject
    public NutritionalValueCtrl(IngredientServerUtils ingredientServerUtils,
                                RecipeServerUtils recipeServerUtils) {
        this.ingredientServerUtils = ingredientServerUtils;
        this.recipeServerUtils = recipeServerUtils;
    }
    // Buttons
    @FXML
    private Button addIngredientModeButton;
    @FXML
    private Button addIngredientButton;
    @FXML
    private Button removeIngredientButton;

    // TextFields
    @FXML
    private TextField ingredientName;
    @FXML
    private TextField carbValue;
    @FXML
    private TextField fatValue;
    @FXML
    private TextField proteinValue;

    // Labels
    @FXML
    private Label kcalValueLabel;
    @FXML
    private Label RecipeUsageValueLabel;

    // Center Pane & StackPane
    @FXML
    private StackPane stackPane;
    @FXML
    private Pane noIngredientPane;
    @FXML
    private Pane ingredientDetailsPane;
    @FXML
    private Pane addIngredientPane;

    // Labels inside noIngredientPane
    @FXML
    private Label noIngredientTitle;
    @FXML
    private Label noIngredientSubtitle;

    // Sidebar
    @FXML
    private ListView<Ingredient> sidebarList;

    //Text fields inside addIngredientPane
    @FXML
    private TextField newIngName;
    @FXML
    private TextField newIngProteins;
    @FXML
    private TextField newIngFats;
    @FXML
    private TextField newIngCarbs;

    //Edit Ingredient buttons
    @FXML
    private Button editIngredientButton;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;

    /**
     * This method initializes the NutritionalValueUI controller
     *
     * This method is automatically called by FXML loader after the controller's
     * root element has been completely processed. It loads the ingredients from
     * the server and places them on the sideBar. It selects the corresponding
     * pane depending on the amount of Ingredients in the app.
     */
    @FXML
    public void initialize() {
        // Initially show/hide panes based on list selection
        updateStackPaneVisibility();

        defaultMode();

        // Listen for selection changes
        sidebarList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            showIngredientDetails(newSelection);
            updateStackPaneVisibility();
        });

        // Listen for items added/removed in the list
        sidebarList.getItems().addListener((ListChangeListener<Object>) change -> {
            updateStackPaneVisibility();
        });

        bindLabelFontSize(noIngredientTitle, 70);
        bindLabelFontSize(noIngredientSubtitle, 80);

        loadIngredientsFromServer();
    }

    /**
     * Loads all the ingredients from the server into the UI. It places them in
     * the sideBarList.
     */
    private void loadIngredientsFromServer() {
        try {
            var ingredients = ingredientServerUtils.getAllIngredients();

            sidebarList.getItems().setAll(ingredients);

            if (!ingredients.isEmpty()) {
                Ingredient first = ingredients.get(0);
                sidebarList.getSelectionModel().select(first);
                showIngredientDetails(first);
            } else {
                showIngredientDetails(null);
            }

            updateStackPaneVisibility();
        } catch (ProcessingException e) {
            showError("Error loading ingredients",
                    "could not load ingredients from the server" + e.getMessage());
            sidebarList.getItems().clear();
            showIngredientDetails(null);
            updateStackPaneVisibility();
        }
    }

    /**
     * It sets the panes depending on the amount of ingredients that are in the
     * database.
     *
     */
    private void updateStackPaneVisibility() {
        boolean hasSelection = sidebarList.getSelectionModel().getSelectedItem() != null;
        boolean hasItems = !sidebarList.getItems().isEmpty();

        if (!hasItems) {
            noIngredientPane.setVisible(true);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(false);
        } else {
            noIngredientPane.setVisible(false);
            addIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(true);
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
        }
    }

    /**
     * It sets the corresponding pane and shows the details of the ingredient
     * that is selected. It makes the TextFields accessible for the user and
     * makes visible and accessible the edit, add and remove buttons. It also
     * sets to normal the status of the remove button if it was in the add
     * ingredient mode.
     */
    private void defaultMode() {
        updateStackPaneVisibility();
        showIngredientDetails(sidebarList.getSelectionModel().getSelectedItem());

        ingredientName.setDisable(true);
        fatValue.setDisable(true);
        proteinValue.setDisable(true);
        carbValue.setDisable(true);
        sidebarList.setDisable(false);

        addIngredientButton.setDisable(false);
        removeIngredientButton.setDisable(false);
        removeIngredientButton.setOnAction(event -> removeIngredient());
        removeIngredientButton.setText("-");

        editIngredientButton.setVisible(true);
        cancelButton.setVisible(false);
        confirmButton.setVisible(false);
    }

    /**
     * It it actioned when the user wants to add an ingredient.
     *
     * It makes all panes invisibles except the addIngredientPane and changes
     * the remove button function to the defaultMode method and the text of
     * button to a back arrow.
     */
    private void addMode() {
        if (!addIngredientPane.isVisible()) {
            noIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(true);
            sidebarList.setDisable(true);
        }
    }

    /**
     * It's activated when the suer wants to edit an ingredient
     *
     * All buttons except the close button are disabled and a confirm and cancel
     * button appear. The edit button disapears
     */
    private void editMode() {
        ingredientName.setDisable(false);
        fatValue.setDisable(false);
        proteinValue.setDisable(false);
        carbValue.setDisable(false);
        sidebarList.setDisable(true);
        addIngredientButton.setDisable(true);
        removeIngredientButton.setDisable(true);

        editIngredientButton.setVisible(false);
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    /**
     * Shows the values of the selected ingredient in the corresponding fields.
     *
     * @param ingredient the ingredient that is selected
     */
    private void showIngredientDetails(Ingredient ingredient) {
        if (ingredient == null) {
            ingredientName.clear();
            proteinValue.clear();
            fatValue.clear();
            carbValue.clear();
            kcalValueLabel.setText("0");
            RecipeUsageValueLabel.setText("Used in 0 recipes");
            return;
        }

        ingredientName.setText(ingredient.getName());
        proteinValue.setText(Double.toString(ingredient.getProteinPer100g()));
        fatValue.setText(Double.toString(ingredient.getFatPer100g()));
        carbValue.setText(Double.toString(ingredient.getCarbsPer100g()));
        kcalValueLabel.setText(Double.toString(ingredient.getKcalPer100g()));

        updateUsageLabel(ingredient);

    }

    // Dynamic font sizing.
    private void bindLabelFontSize(Label label, double growthFactor) {
        double baseFontSize = 9;

        label.fontProperty().bind(Bindings.createObjectBinding(() -> {
            double size = baseFontSize + 
                Math.min(noIngredientPane.getWidth(), noIngredientPane.getHeight()) / growthFactor;
            return new Font("System", size);
        }, noIngredientPane.widthProperty(), noIngredientPane.heightProperty()));
    }

    /**
     * A method that creates an error alert with a heading and content
     *
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

    /**
     * When the add button is pressed for a second time after entering the
     * values in the boxes it creates a new ingredient and also updates the
     * sideBar with the new ingredient.
     */
    @FXML
    void addIngredient() {
        if (!addIngredientPane.isVisible()) {
            addMode();
            return;
        }

        try {
            String name = newIngName.getText().trim();
            double protein = Double.parseDouble(newIngProteins.getText().trim());
            double carb = Double.parseDouble(newIngCarbs.getText().trim());
            double fat = Double.parseDouble(newIngFats.getText().trim());

            if (name.isEmpty()) {
                showError("Error", "Name cannot be empty");
                return;
            }
            if (protein < 0 || carb < 0 || fat < 0) {
                showError("Error", "Protein, Carbohydrates or fats cannot be negative");
                return;
            }

            Ingredient toCreate = new Ingredient(name, protein, fat, carb);
            System.out.println("Creating ingredient: " + toCreate.getName());

            Long id = ingredientServerUtils.createIngredient(toCreate);
            System.out.println("Created ingredient with ID: CTRL " + id);
            toCreate.setId(id);

            System.out.println("Saved ingredient ID: " + toCreate.getId());

            sidebarList.getItems().add(toCreate);
            sidebarList.getSelectionModel().select(toCreate);

            newIngName.clear();
            newIngProteins.clear();
            newIngFats.clear();
            newIngCarbs.clear();

            defaultMode();

        } catch (NumberFormatException e) {
            showError("Wrong format", "Please enter valid numbers for protein, carbs and fat.");
        } catch (IllegalArgumentException e) {
            showError("Error", e.getMessage());
        } catch (ProcessingException e) {
            showError("Server error while making ingredient", e.getMessage());
        }
    }

    /**
     * It removes the selected ingredient from the database and from the
     * sideBar. Before deleting it asks the user for confirmation.
     */
    @FXML
    void removeIngredient() {
        if (!addIngredientPane.isVisible()) {
            return;
        }

        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No ingredient selected", "Select an ingredient to remove");
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Ingredient");
        confirm.setHeaderText("Are you sure you want to delete " + selected.getName() + "?");
        confirm.setContentText("This will remove the ingredient from the database");
        var result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            ingredientServerUtils.deleteIngredient(selected.getId());
            sidebarList.getItems().remove(selected);
            updateStackPaneVisibility();
        } catch (NoSuchElementException e) {
            showError("Ingredient not found on server", "It seems the ingredient was already removed");
            sidebarList.getItems().remove(selected);
            updateStackPaneVisibility();
        } catch (ProcessingException e) {
            showError("Error deleting Ingredient", "Server error" + e.getMessage());
        }
    }

    /**
     * It enters edit mode and shows the details of the selected ingredient.
     */
    @FXML
    void editIngredient() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        showIngredientDetails(selected);
        if (selected == null) {
            showError("No ingredient selected", "Select an ingredient to edit");
        }

        editMode();
    }

    /**
     * When the user presses confirm the new values for the ingredient replace
     * the old values. If the name is changed it will also update it in the
     * sideBar
     */
    @FXML
    void confirmEdit() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No ingredient selected", "Select an ingredient to edit");
            return;
        }

        try {
            String name = ingredientName.getText().trim();
            double protein = Double.parseDouble(proteinValue.getText().trim());
            double carb = Double.parseDouble(carbValue.getText().trim());
            double fat = Double.parseDouble(fatValue.getText().trim());

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (protein < 0 || carb < 0 || fat < 0) {
                throw new IllegalArgumentException("Protein, carbohydrates or fats cannot be negative");
            }

            selected.setName(name);
            selected.setProteinPer100g(protein);
            selected.setCarbsPer100g(carb);
            selected.setFatPer100g(fat);

            ingredientServerUtils.replaceIngredient(selected.getId(), selected);

            sidebarList.refresh();

            sidebarList.getSelectionModel().select(selected);

            showIngredientDetails(selected);

            defaultMode();

        } catch (NumberFormatException e) {
            showError("Invalid input", "Please enter valid numbers");
        } catch (IllegalArgumentException e) {
            showError("Invalid ingredient", e.getMessage());
        } catch (ProcessingException e) {
            showError("Error editing ingredient", e.getMessage());
        }
    }

    /**
     * When the user presses cancel, the user gets out of the editing mode to
     * the default mode.
     */
    @FXML
    void cancelEdit() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        showIngredientDetails(selected);

        defaultMode();
    }

    /**
     * This method updates the usageLabel to the one from the ingredient that is
     * selected.
     *
     * @param ingredient The ingredient to count in recipes
     */
    private void updateUsageLabel(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null) {
            RecipeUsageValueLabel.setText("Used in 0 recipes");
            return;
        }

        try {
            long usage = getUsageCount(ingredient.getId());
            RecipeUsageValueLabel.setText("Used in " + usage + " recipes");
        } catch (NoSuchElementException e) {
            RecipeUsageValueLabel.setText("Used in 0 recipes");
        } catch (ProcessingException e) {
            RecipeUsageValueLabel.setText("Used in ? recipes");
        }
    }
    private int getUsageCount(Long ingredientId) {
        int usage = 0;

        List<Recipe> recipes = recipeServerUtils.getAllRecipes();

        for (Recipe r : recipes) {
            if (r.getIngredients() == null) continue;

            for (RecipeIngredient rIng : r.getIngredients()) {
                if (rIng.getIngredient() != null && Objects.equals(rIng.getIngredient().getId(), ingredientId)) {
                    usage++;
                    break; // count recipe only once
                }
            }
        }

        return usage;
    }


    @FXML
    void updateIngredientName() {
        System.out.println("User wants to update ingredient name.");
    }

    @FXML
    void updateIngredientProtein() {
        System.out.println("User wants to update ingredient protein.");
    }

    @FXML
    void updateIngredientFat() {
        System.out.println("User wants to update ingredient fat.");
    }

    @FXML
    void updateIngredientCarbs() {
        System.out.println("User wants to update ingredient carbs.");
    }

    @FXML
    void seeSelectedIngredient() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        System.out.println("Selected ingredient: " + selected.getId());
    }

}
