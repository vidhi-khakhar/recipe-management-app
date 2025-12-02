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

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AddButtonCtrl {
    //Elements of the UI
    @FXML
    private Button recipeAddButton;

    @FXML
    private TextField recipeInput;

    @FXML
    private Label statusLabel;

    //Search method
    @FXML
    private void recipeAddRequest() {
        String query = recipeInput.getText();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a query.");
        } else {
            System.out.println("Searching for: " + query);
            // Actual functionality should be added here!
        }
    }
}

