package client.scenes;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.shopping.RecipeIngredientData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    private final ConfigService configService;
    private ResourceBundle bundle;

    private ObservableList<String> commonUnits;

    @Inject
    public IngredientItemCtrl(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Initializes the UI components and sets up listeners.
     */
    @FXML
    public void initialize() {
        bundle = getTranslationsBundle();

        commonUnits = FXCollections.observableArrayList(
                "g", "kg", "mg", "oz", "lb",
                "ml", "l", "fl oz", "cup", "tsp", "tbsp", "pinch",
                "whole", "piece", "slice", "clove", "dash",
                bundle.getString("label.unitToTaste"),
                bundle.getString("label.unitAsNeeded"),
                bundle.getString("label.unitCustomPlaceholder")
        );

        unitComboBox.setItems(commonUnits);
        unitComboBox.setEditable(true);

        nameField.textProperty().addListener((obs, o, n) -> handleNameFieldChange(n));
        amountField.textProperty().addListener((obs, o, n) -> handleAmountFieldChange(n));
        unitComboBox.valueProperty().addListener((obs, o, n) -> handleUnitComboBoxChange(n));
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
                    ingredientData.setAmount(parsed.doubleValue());
                    ingredientData.setFormal(true);
                }
            } catch (ParseException e) {
                ingredientData.setAmount(0.0);
                if (newVal.trim().isEmpty()) ingredientData.setFormal(false);
            }
            notifyChange();
        }
    }

    private void handleUnitComboBoxChange(String newVal) {
        if (ingredientData == null || updatingFields) return;

        String unit = newVal != null ? newVal.trim() : "";
        ingredientData.setUnit(unit);

        if (isInformalUnit(unit)) {
            ingredientData.setFormal(false);
            ingredientData.setInformal(unit);
            disableAmount();
            helperLabel.setText(
                    bundle.getString("label.ingredientHelperInformal").replace("{0}", unit)
            );
        } else if (unit.equals(bundle.getString("label.unitCustomPlaceholder"))) {
            updatingFields = true;
            unitComboBox.getEditor().clear();
            unitComboBox.getEditor().requestFocus();
            updatingFields = false;
            helperLabel.setText(bundle.getString("label.ingredientHelperTypeCustom"));
        } else {
            ingredientData.setFormal(true);
            ingredientData.setInformal(null);
            enableAmount();
            helperLabel.setText(
                    unit.isEmpty()
                            ? bundle.getString("label.ingredientHelperSelectUnit")
                            : bundle.getString("label.ingredientHelperFormal")
            );
        }
        notifyChange();
    }

    private void handleCustomUnitAction() {
        if (ingredientData == null || updatingFields) return;

        String unit = unitComboBox.getEditor().getText().trim();
        boolean isCustom = !unit.isEmpty() && !unit.equals(
            bundle.getString("label.unitCustomPlaceholder")) && !commonUnits.contains(unit);
        if (isCustom) {
            ingredientData.setUnit(unit);
            ingredientData.setFormal(false);
            ingredientData.setInformal(unit);
            disableAmount();
            helperLabel.setText(
                    bundle.getString("label.ingredientHelperCustom").replace("{0}", unit)
            );
            notifyChange();
        }
    }

    private void disableAmount() {
        amountField.clear();
        amountField.setDisable(true);
        amountField.setStyle("-fx-opacity: 0.5;");
    }

    private void enableAmount() {
        amountField.setDisable(false);
        amountField.setStyle("");
    }

    /**
     * Handles remove button click.
     * @param event the action event
     */
    @FXML
    private void removeIngredient(ActionEvent event) {
        if (onRemove != null) onRemove.run();
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
            enableAmount();
            helperLabel.setText(bundle.getString("label.ingredientHelperFormal"));
        } else {
            disableAmount();
            if (ingredientData.getInformal() != null) {
                unitComboBox.setValue(ingredientData.getInformal());
                helperLabel.setText(
                        bundle.getString("label.ingredientHelperInformal")
                                .replace("{0}", ingredientData.getInformal())
                );
            } else {
                unitComboBox.setValue("");
                enableAmount();
                helperLabel.setText(bundle.getString("label.ingredientHelperFormal"));
            }
        }

        updatingFields = false;
    }

    private boolean isInformalUnit(String unit) {
        return unit != null && (
                unit.equals(bundle.getString("label.unitToTaste")) ||
                unit.equals(bundle.getString("label.unitAsNeeded")) ||
                unit.equals(bundle.getString("label.unitToPreference"))
            );
    }

    private void notifyChange() {
        if (onUpdate != null) onUpdate.accept(ingredientData.copy());
        if (onChange != null) onChange.run();
    }

    /**
     * Gets the current ingredient data.
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
     * @return true if valid, false otherwise
     */
    public boolean validateIngredient() {
        if (nameField.getText().trim().isEmpty()) {
            nameField.setStyle("-fx-border-color: red;");
            return false;
        }
        nameField.setStyle("");

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

    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return ResourceBundle.getBundle("client.scenes.Translations", new Locale(languageCode));
        } catch (Exception e) {
            return ResourceBundle.getBundle("client.scenes.Translations", Locale.ENGLISH);
        }
    }

    /**
     * Initializes the controller with ingredient data.
     * @param ingredientData the ingredient data to display/edit
     */
    public void setIngredientData(RecipeIngredientData ingredientData) {
        this.ingredientData = ingredientData;
        updateFields();
    }

    /**
     * Sets the callback to be executed when the remove button is clicked.
     * @param onRemove the callback to execute
     */
    public void setOnRemove(Runnable onRemove) {
        this.onRemove = onRemove;
    }

    /**
     * Sets the callback to be executed when any field is changed.
     * @param onChange the callback to execute
     */
    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    /**
     * Sets the callback to be executed when ingredient data is updated.
     * @param onUpdate the callback that receives the updated ingredient data
     */
    public void setOnUpdate(java.util.function.Consumer<RecipeIngredientData> onUpdate) {
        this.onUpdate = onUpdate;
    }
}
