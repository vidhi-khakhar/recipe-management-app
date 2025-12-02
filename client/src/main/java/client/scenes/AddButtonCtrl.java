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

    //Adding Recipe methods
    @FXML
    private void recipeAddRequest() {
        String query = recipeInput.getText();
        if (query == null || query.isEmpty()) {
            System.out.println("Please enter a query."); // Can be removed in the future
            statusLabel.setText("Please enter a query.");
            statusLabel.setStyle("-fx-text-fill: red;");
        } else {
            statusLabel.setText("");
            System.out.println("Adding request for: " + query); // Can be removed in the future
            // Actual functionality should be added here!
        }
    }
}

