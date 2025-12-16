package client.scenes;

import com.google.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import client.utils.IngredientServerUtils;
import commons.Ingredient;
import jakarta.ws.rs.ProcessingException;

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
        System.out.println("User wants to remove an ingredient.");
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
