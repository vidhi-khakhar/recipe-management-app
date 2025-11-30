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

    @FXML
    private Button addIngredientButton;

    @FXML
    private TextField carbValue;

    @FXML
    private TextField fatValue;

    @FXML
    private TextField ingredientName;

    @FXML
    private Label kcalValue;

    @FXML
    private TextField proteinValue;

    @FXML
    private Button removeIngredientButton;

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
    void addIngredient(ActionEvent event) {
        System.out.println("User wants to add an ingredient.");
    }

}
