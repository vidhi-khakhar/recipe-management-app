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
    @FXML private Label kcalValue;
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

        // Listen for selection changes
        sidebarList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateStackPaneVisibility();
        });

        // Listen for items added/removed in the list
        sidebarList.getItems().addListener((ListChangeListener<Object>) change -> {
            updateStackPaneVisibility();
        });

        bindLabelFontSize(noIngredientTitle, 70);  
        bindLabelFontSize(noIngredientSubtitle, 80);
    }

    private void updateStackPaneVisibility() {
        boolean hasSelection = sidebarList.getSelectionModel().getSelectedItem() != null;
        boolean hasItems = !sidebarList.getItems().isEmpty();

        if (!hasItems || !hasSelection) {
            noIngredientPane.setVisible(true);
            ingredientDetailsPane.setVisible(false);
            addIngredientPane.setVisible(false);
        } else {
            noIngredientPane.setVisible(false);
            addIngredientPane.setVisible(false);
            ingredientDetailsPane.setVisible(true);
        }
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
                    throw new IllegalArgumentException("Name cannot be empty");
                }

                if (protein < 0 || carb < 0 || fat < 0) {
                    throw new IllegalArgumentException("Protein, Carbohydrates or fats cannot be negative");
                }

                Ingredient ingredient = new Ingredient(name, protein, carb, fat);

                Long id = ingredientServerUtils.createIngredient(ingredient);
                ingredient.setId(id);

                sidebarList.getItems().add(ingredient);
                sidebarList.getSelectionModel().select(ingredient);

                newIngName.clear();
                newIngProteins.clear();
                newIngFats.clear();
                newIngCarbs.clear();

                addIngredientPane.setVisible(false);
                ingredientDetailsPane.setVisible(true);

            }catch(NumberFormatException e) {
                System.err.println("Please enter valid numbers for protein, carbs and fat.");
            }catch(IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }catch(jakarta.ws.rs.ProcessingException e) {
                System.err.println("Server error while making ingredient: " + e.getMessage());
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
