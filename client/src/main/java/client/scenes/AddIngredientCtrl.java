package client.scenes;

import javafx.fxml.FXML;

public class AddIngredientCtrl {
    @FXML
    private void saveButtonClicked() {
        System.out.println("User saved ingredient to recipe.");
    }

    @FXML
    private void cancelButtonClicked() {
        System.out.println("User canceled adding ingredient.");
    }
}
