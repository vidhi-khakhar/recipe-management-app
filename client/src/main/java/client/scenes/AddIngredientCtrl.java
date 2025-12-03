package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AddIngredientCtrl {
    // Buttons
    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    // Button Methods
    @FXML
    private void saveButtonClicked() {
        System.out.println("User saved ingredient to recipe.");

        //TODO: Get user input and create RecipeIngredient 

        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Discards all edits and closes popup.
     */
    @FXML
    private void cancelButtonClicked() {
        System.out.println("User canceled adding ingredient.");

        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}
