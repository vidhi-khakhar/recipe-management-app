package client.scenes;

import client.shopping.RecipeIngredientData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Controller for individual ingredient items in the recipe shopping list.
 * Handles editing of ingredient name, amount, and unit.
 */
public class IngredientItemCtrl {

    @FXML
    private TextField nameField;

    @FXML
    private TextField amountField;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private Button removeButton;

    private RecipeIngredientData ingredientData;
    private Runnable onRemove;
    private Runnable onChange;
    private boolean updatingFields = false;

    // Common units for the dropdown
    private static final ObservableList<String> COMMON_UNITS = FXCollections.observableArrayList(
        "g", "kg", "mg", "oz", "lb",
        "ml", "l", "fl oz", "cup", "tsp", "tbsp", "pinch",
        "whole", "piece", "slice", "clove", "dash",
        "", "to taste", "as needed"
    );

    /**
     * Initializes the controller with ingredient data.
     * 
     * @param ingredientData the ingredient data to display/edit
     */
    public void setIngredientData(RecipeIngredientData ingredientData) {
        this.ingredientData = ingredientData;
        updateFields();
    }

    /**
     * Sets the callback to be executed when the remove button is clicked.
     * 
     * @param onRemove the callback to execute
     */
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    /**
     * Sets the callback to be executed when any field is changed.
     * 
     * @param onChange the callback to execute
     */
    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    /**
     * Initializes the UI components.
     */
    @FXML
    public void initialize() {
        // Setup unit combobox
        unitComboBox.setItems(COMMON_UNITS);
        unitComboBox.setEditable(true); // Allow custom units
        
        // Add listeners for real-time updates
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (ingredientData != null && !updatingFields) {
                ingredientData.setName(newVal.trim());
                notifyChange();
            }
        });

        amountField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (ingredientData != null && !updatingFields) {
                try {
                    double amount = Double.parseDouble(newVal);
                    ingredientData.setAmount(amount);
                    ingredientData.setFormal(true); // Setting amount makes it formal
                } catch (NumberFormatException e) {
                    ingredientData.setAmount(0.0);
                    if (newVal.trim().isEmpty()) {
                        ingredientData.setFormal(false);
                    }
                }
                notifyChange();
            }
        });

        unitComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (ingredientData != null && !updatingFields) {
                String unit = newVal != null ? newVal.trim() : "";
                ingredientData.setUnit(unit);
                
                // Check if this should be treated as informal
                if (isInformalUnit(unit)) {
                    ingredientData.setFormal(false);
                    ingredientData.setInformal(unit);
                } else {
                    ingredientData.setFormal(true);
                    ingredientData.setInformal(null);
                }
                notifyChange();
            }
        });
    }

    /**
     * Handles remove button click.
     * 
     * @param event the action event
     */
    @FXML
    private void removeIngredient(ActionEvent event) {
        if (onRemove != null) {
            onRemove.run();
        }
    }

    /**
     * Updates the UI fields to match the ingredient data.
     */
    private void updateFields() {
        if (ingredientData == null) return;

        updatingFields = true;
        nameField.setText(ingredientData.getName());
        
        if (ingredientData.isFormal()) {
            amountField.setText(String.format("%.1f", ingredientData.getAmount()));
            unitComboBox.setValue(ingredientData.getUnit());
        } else {
            amountField.setText("");
            if (ingredientData.getInformal() != null) {
                unitComboBox.setValue(ingredientData.getInformal());
            } else {
                unitComboBox.setValue("");
            }
        }
        updatingFields = false;
    }

    /**
     * Checks if a unit should be treated as informal.
     * 
     * @param unit the unit to check
     * @return true if informal, false if formal
     */
    private boolean isInformalUnit(String unit) {
        return unit != null && (unit.equals("to taste") || 
                               unit.equals("as needed") || 
                               unit.equals("to preference"));
    }

    /**
     * Notifies listeners of changes.
     */
    private void notifyChange() {
        if (onChange != null) {
            onChange.run();
        }
    }

    /**
     * Gets the current ingredient data.
     * 
     * @return the ingredient data
     */
    public RecipeIngredientData getIngredientData() {
        return ingredientData;
    }

    /**
     * Sets focus to the name field for editing.
     */
    public void focusNameField() {
        nameField.requestFocus();
        nameField.selectAll();
    }

    /**
     * Validates the current ingredient data.
     * 
     * @return true if valid, false otherwise
     */
    public boolean validateIngredient() {
        if (nameField.getText().trim().isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            return false;
        }
        
        nameField.setStyle("");
        
        if (ingredientData.isFormal()) {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (amount <= 0) {
                    amountField.setStyle("-fx-border-color: red;");
                    return false;
                }
            } catch (NumberFormatException e) {
                amountField.setStyle("-fx-border-color: red;");
                return false;
            }
        }
        
        amountField.setStyle("");
        return true;
    }

    /**
     * Clears validation styling.
     */
    public void clearValidation() {
        nameField.setStyle("");
        amountField.setStyle("");
    }
}