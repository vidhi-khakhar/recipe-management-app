package client.scenes;

import commons.Recipe;
import commons.RecipeCollection;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddButtonCtrl {
    private MainCtrl mainCtrl;

    @FXML
    private Button recipeAddButton;

    @FXML
    private TextField recipeInput;

    @FXML
    private Label statusLabel;

    public void setMainCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    //Adding Recipe methods
    @FXML
    private void recipeAddRequest() {
        String query = recipeInput.getText().trim();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a query."); // Can be removed in the future
            statusLabel.setText("Please enter a query.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            System.out.println("Adding request for: " + query); // Can be removed in the future
            Recipe recipe = new Recipe(query);
            mainCtrl.addRecipe(recipe); // update main
            recipeInput.clear();
            statusLabel.setText("Recipe added!");
            statusLabel.setStyle("-fx-text-fill: green;");
        }
    }

}

