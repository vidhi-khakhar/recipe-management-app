package client.scenes;

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

public class NutritionalValueCtrl {
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

    // Labels inside noIngredientPane
    @FXML private Label noIngredientTitle;
    @FXML private Label noIngredientSubtitle;

    // Sidebar
    @FXML private ListView<?> sidebarList;

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
        } else {
            noIngredientPane.setVisible(false);
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
        System.out.println("User wants to add an ingredient.");
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
