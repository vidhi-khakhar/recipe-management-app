package client.scenes;

import client.shopping.RecipeIngredientData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.text.NumberFormat;
import java.text.ParseException;

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

    @FXML
    private Label helperLabel;

    private RecipeIngredientData ingredientData;
    private Runnable onRemove;
    private Runnable onChange;
    private java.util.function.Consumer<RecipeIngredientData> onUpdate;
    private boolean updatingFields = false;
    private final NumberFormat numberFormat = NumberFormat.getNumberInstance();

    // Common units for the dropdown
    private static final ObservableList<String> COMMON_UNITS = FXCollections.observableArrayList(
        "g", "kg", "mg", "oz", "lb",
        "ml", "l", "fl oz", "cup", "tsp", "tbsp", "pinch",
        "whole", "piece", "slice", "clove", "dash",
        "to taste (informal)", "as needed (informal)",
        "Type custom unit..."
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
     * Sets the callback to be executed when ingredient data is updated
     *
     * @param onUpdate the callback that receives the updated ingredient data
     */
    public void setOnUpdate(java.util.function.Consumer<RecipeIngredientData> onUpdate) {
        this.onUpdate = onUpdate;
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
        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                handleNameFieldChange(newVal));

        amountField.textProperty().addListener((obs, oldVal, newVal) ->
                handleAmountFieldChange(newVal));

        unitComboBox.valueProperty().addListener((obs, oldVal, newVal) ->
                handleUnitComboBoxChange(newVal));

        // Handle custom typed units
        unitComboBox.setOnAction(e -> handleCustomUnitAction());
    }

    private void handleNameFieldChange(String newVal) {
        if (ingredientData != null && !updatingFields) {
            ingredientData.setName(newVal.trim());
            notifyChange();
        }
    }

    private void handleAmountFieldChange(String newVal) {
        if (ingredientData != null && !updatingFields) {
            try {
                if (newVal.trim().isEmpty()) {
                    ingredientData.setAmount(0.0);
                    ingredientData.setFormal(false);
                } else {
                    Number parsed = numberFormat.parse(newVal);
                    double amount = parsed.doubleValue();
                    ingredientData.setAmount(amount);
                    ingredientData.setFormal(true);
                }
            } catch (ParseException e) {
                ingredientData.setAmount(0.0);
                if (newVal.trim().isEmpty()) {
                    ingredientData.setFormal(false);
                }
            }
            notifyChange();
        }
    }

    private void handleUnitComboBoxChange(String newVal) {
        if (ingredientData != null && !updatingFields) {
            String unit = newVal != null ? newVal.trim() : "";
            
            // Always update the unit in the data model
            ingredientData.setUnit(unit);
            
            // Check if this should be treated as informal
            if (isInformalUnit(unit) || unit.equals("Type custom unit...")) {
                if (unit.equals("Type custom unit...")) {
                    // Clear and focus for typing
                    updatingFields = true;
                    unitComboBox.getEditor().clear();
                    unitComboBox.hide();
                    unitComboBox.getEditor().requestFocus();
                    updatingFields = false;
                    helperLabel.setText("Type custom unit for informal measurement");
                    return;
                }
                ingredientData.setFormal(false);
                ingredientData.setInformal(unit);
                amountField.setDisable(true);
                amountField.setStyle("-fx-opacity: 0.5;");
                helperLabel.setText("Informal: " + unit + " (amount not needed, scaling unavailable)");
            } else {
                ingredientData.setFormal(true);
                ingredientData.setInformal(null);
                amountField.setDisable(false);
                amountField.setStyle("");
                if (unit.isEmpty()) {
                    helperLabel.setText("Select a unit or type custom unit for informal measurement");
                } else {
                    helperLabel.setText("Enter amount and select unit for formal measurement");
                }
            }
            notifyChange();
        }
    }

    private void handleCustomUnitAction() {
        if (ingredientData != null && !updatingFields) {
            String unit = unitComboBox.getEditor().getText().trim();
            boolean isCustom = !unit.isEmpty()
                    && !unit.equals("Type custom unit...")
                    && !COMMON_UNITS.contains(unit);
            if (isCustom) {
                // Custom unit typed
                ingredientData.setUnit(unit);
                ingredientData.setFormal(false);
                ingredientData.setInformal(unit);
                amountField.setDisable(true);
                amountField.setStyle("-fx-opacity: 0.5;");
                helperLabel.setText("Custom informal: " + unit + " (amount not needed, scaling unavailable)");
                notifyChange();
            }
        }
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
            amountField.setText(numberFormat.format(ingredientData.getAmount()));
            unitComboBox.setValue(ingredientData.getUnit());
            amountField.setDisable(false);
            amountField.setStyle("");
            helperLabel.setText("Enter amount and select unit for formal measurement");
        } else {
            amountField.setText("");
            if (ingredientData.getInformal() != null) {
                unitComboBox.setValue(ingredientData.getInformal());
                amountField.setDisable(true);
                amountField.setStyle("-fx-opacity: 0.5;");
                helperLabel.setText("Informal: " + ingredientData.getInformal() + " (amount not needed, scaling unavailable)");
            } else {
                unitComboBox.setValue("");
                amountField.setDisable(false);
                amountField.setStyle("");
                helperLabel.setText("Enter amount and select unit for formal measurement");
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
        return unit != null && (unit.equals("to taste (informal)") || 
                               unit.equals("as needed (informal)") || 
                               unit.equals("to preference"));
    }

    /**
     * Notifies listeners of changes.
     */
    private void notifyChange() {
        if (onUpdate != null) {
            onUpdate.accept(ingredientData.copy());
        }
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
        
        // Only validate amount for formal ingredients
        if (ingredientData.isFormal() && !amountField.isDisabled()) {
            try {
                Number parsed = numberFormat.parse(amountField.getText());
                double amount = parsed.doubleValue();
                if (amount <= 0) {
                    amountField.setStyle("-fx-border-color: red;");
                    return false;
                }
            } catch (ParseException e) {
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
        if (!amountField.isDisabled()) {
            amountField.setStyle("");
        }
    }
}