package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;


public class NutritionalValueCtrl {
    // Buttons
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
    private Label kcalValue;

    @FXML
    private Label RecipeUsageValueLabel;


    // ListView and Panes
    @FXML
    private ListView<?> sidebarList;

    @FXML
    private StackPane stackPane;

    @FXML
    private Pane noIngredientPane;

    @FXML
    private Pane ingredientDetailsPane;

    @FXML
    public void initialize() {
        // Initially, if list is empty, show "No Ingredient" pane
        updateStackPaneVisibility();

        // Add listener for selection changes
        sidebarList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            updateStackPaneVisibility();
        });

        // Add listener for changes to the list itself
        sidebarList.getItems().addListener((ListChangeListener<Object>) change -> {
            updateStackPaneVisibility();
        });
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


    @FXML
    void addIngredient() {
        System.out.println("User wants to add an ingredient.");
    }

    @FXML
    void removeIngredient() {
        System.out.println("User wants to add an ingredient.");
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
