package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

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
    void addIngredient(ActionEvent event) {

    }

}
