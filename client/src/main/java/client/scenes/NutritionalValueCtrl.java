package client.scenes;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import client.utils.IngredientServerUtils;
import commons.Ingredient;
import jakarta.ws.rs.ProcessingException;

import java.util.NoSuchElementException;

public class NutritionalValueCtrl {

    private final IngredientServerUtils ingredientServerUtils;

    @Inject
    public NutritionalValueCtrl(IngredientServerUtils ingredientServerUtils) {
        this.ingredientServerUtils = ingredientServerUtils;
    }
    // Buttons
    @FXML private Button addIngredientButton;
    @FXML private Button removeIngredientButton;

    // TextFields
    @FXML private TextField ingredientName;
    @FXML private TextField carbValue;
    @FXML private TextField fatValue;
    @FXML private TextField proteinValue;

    // Labels
    @FXML private Label kcalValueLabel;
    @FXML private Label RecipeUsageValueLabel;

    // Center Pane & StackPane
    @FXML private StackPane stackPane;
    @FXML private Pane noIngredientPane;
    @FXML private Pane ingredientDetailsPane;
    @FXML private Pane addIngredientPane;

    // Labels inside noIngredientPane
    @FXML private Label noIngredientTitle;
    @FXML private Label noIngredientSubtitle;

    // Sidebar
    @FXML private ListView<Ingredient> sidebarList;

    //Text fields inside addIngredientPane
    @FXML private TextField newIngName;
    @FXML private TextField newIngProteins;
    @FXML private TextField newIngFats;
    @FXML private TextField newIngCarbs;

    //Edit Ingredient buttons
    @FXML private Button editIngredientButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    @FXML
    public void initialize() {
        // Initially show/hide panes based on list selection
        updateStackPaneVisibility();

        disableDetails();

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

    private void loadIngredientsFromServer(){
        try{
            var ingredients = ingredientServerUtils.getAllIngredients();

            sidebarList.getItems().setAll(ingredients);

            if(!ingredients.isEmpty()){
                Ingredient first = ingredients.get(0);
                sidebarList.getSelectionModel().select(first);
                showIngredientDetails(first);
            }else{
                showIngredientDetails(null);
            }

            updateStackPaneVisibility();
        }catch(ProcessingException e){
            showError("Error loading ingredients",
                    "could not load ingredients from the server" + e.getMessage());
            sidebarList.getItems().clear();
            showIngredientDetails(null);
            updateStackPaneVisibility();
        }
    }

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

    private void disableDetails(){
        ingredientName.setDisable(true);
        fatValue.setDisable(true);
        proteinValue.setDisable(true);
        carbValue.setDisable(true);
        sidebarList.setDisable(false);
        addIngredientButton.setDisable(false);
        removeIngredientButton.setDisable(false);
    }

    private void enableDetails(){
        ingredientName.setDisable(false);
        fatValue.setDisable(false);
        proteinValue.setDisable(false);
        carbValue.setDisable(false);
        sidebarList.setDisable(true);
        addIngredientButton.setDisable(true);
        removeIngredientButton.setDisable(true);
    }

    private void showIngredientDetails(Ingredient ingredient) {
        if(ingredient == null){
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
            double size = baseFontSize + Math.min(noIngredientPane.getWidth(), noIngredientPane.getHeight()) / growthFactor;
            return new Font("System", size);
        }, noIngredientPane.widthProperty(), noIngredientPane.heightProperty()));
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void addIngredient() {
        if(!addIngredientPane.isVisible()) {
            noIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(true);
        }else{

            try {
                String name = newIngName.getText().trim();
                double protein = Double.parseDouble(newIngProteins.getText().trim());
                double carb = Double.parseDouble(newIngCarbs.getText().trim());
                double fat = Double.parseDouble(newIngFats.getText().trim());

                if (name.isEmpty()) {
                    showError("Error", "Name cannot be empty");
                }else if (protein < 0 || carb < 0 || fat < 0) {
                    showError("Error", "Protein, Carbohydrates or fats cannot be negative");
                }else{
                    Ingredient ingredient = new Ingredient(name, protein, carb, fat);

                    Long id = ingredientServerUtils.createIngredient(ingredient);
                    ingredient.setId(id);

                    sidebarList.getItems().add(ingredient);
                    sidebarList.getSelectionModel().select(ingredient);

                    newIngName.clear();
                    newIngProteins.clear();
                    newIngFats.clear();
                    newIngCarbs.clear();

                    updateStackPaneVisibility();
                }

            }catch(NumberFormatException e) {
                showError("Wrong format","Please enter valid numbers for protein, carbs and fat.");
            }catch(IllegalArgumentException e) {
                showError("Error", e.getMessage());
            }catch(jakarta.ws.rs.ProcessingException e) {
                showError("Server error while making ingredient: ", e.getMessage());
            }



        }

    }

    @FXML
    void removeIngredient() {
        Ingredient selected =  sidebarList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No ingredient selected", "Select an ingredient to remove");
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Ingredient");
        confirm.setHeaderText("Are you sure you want to delete " + selected.getName() + "?");
        confirm.setContentText("This will remove the ingredient from the database");
        var result = confirm.showAndWait();

        if(result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            ingredientServerUtils.deleteIngredient(selected.getId());
            sidebarList.getItems().remove(selected);
            updateStackPaneVisibility();
        } catch (NoSuchElementException e){
            showError("Ingredient not found on server", "It seems the ingredient was already removed");
            sidebarList.getItems().remove(selected);
            updateStackPaneVisibility();
        } catch (ProcessingException e) {
            showError("Error deleting Ingredient", "Server error" + e.getMessage());
        }
    }

    @FXML
    void editIngredient() {
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        showIngredientDetails(selected);
        if (selected == null) {
            showError("No ingredient selected", "Select an ingredient to edit");
        }

        enableDetails();
        editIngredientButton.setVisible(false);
        confirmButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    @FXML
    void confirmEdit(){
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
                throw new IllegalArgumentException("Protein, Carbohydrates or fats cannot be negative");
            }

            Ingredient updated = new Ingredient(name, protein, fat, carb);
            updated.setId(selected.getId());

            ingredientServerUtils.replaceIngredient(selected.getId(), updated);

            int index = sidebarList.getItems().indexOf(selected);
            sidebarList.getItems().set(index, updated);
            sidebarList.getSelectionModel().select(updated);

            showIngredientDetails(updated);

            disableDetails();
            confirmButton.setVisible(false);
            cancelButton.setVisible(false);
            editIngredientButton.setVisible(true);
        }catch(NumberFormatException e) {
            showError("Invalid input", "Please enetr valid inputs for macronutrients");
        }catch(IllegalArgumentException e) {
            showError("Invalid Ingredient", e.getMessage());
        }catch(ProcessingException e) {
            showError("Error deleting ingredient", "Server error" + e.getMessage());
        }
    }

    @FXML
    void cancelEdit(){
        Ingredient selected = sidebarList.getSelectionModel().getSelectedItem();
        showIngredientDetails(selected);
        disableDetails();
        confirmButton.setVisible(false);
        cancelButton.setVisible(false);
        editIngredientButton.setVisible(true);
    }


    private void updateUsageLabel(Ingredient ingredient) {
        if (ingredient == null || ingredient.getId() == null) {
            RecipeUsageValueLabel.setText("Used in 0 recipes");
            return;
        }

        try {
            long usage = ingredientServerUtils.getIngredientUsageCount(ingredient.getId());
            RecipeUsageValueLabel.setText("Used in " + usage + " recipes");
        }catch(NoSuchElementException e) {
            RecipeUsageValueLabel.setText("Used in 0 recipes");
        }catch(ProcessingException e) {
            RecipeUsageValueLabel.setText("Used in ? recipes");
        }
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
}
