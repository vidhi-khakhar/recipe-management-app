package client.scenes;

import commons.Ingredient;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.List;

public class AddIngredientCtrl {
    // UI Elements
    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private ChoiceBox<Unit> unitChoiceBox;

    @FXML
    private ChoiceBox<String> unitTypeChoiceBox;

    /**
     * Explanation of unitTypeChoiceBox:
     *      selectionModel - in the case of a ChoiceBox it is a SingleSelectionModel
     *      selectedItemProperty - indicates the currently selected item in the selection model
     *      addListener - function that runs whenever a change is observed
     */
    @FXML
    public void initialize() {
        unitTypeChoiceBox.getItems().addAll("Formal", "Informal");

        unitTypeChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    updateUnitChoiceBoxVisibility(newValue);
                });
    }

    public void updateUnitChoiceBoxVisibility(String unitType) {
        if (unitType.equals("Formal")) {
            unitChoiceBox.setItems(FXCollections.observableArrayList(Unit.values()));
            unitChoiceBox.setVisible(true);
        } else if (unitType.equals("Informal")) {
            unitChoiceBox.setVisible(false);
        }
    }

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
