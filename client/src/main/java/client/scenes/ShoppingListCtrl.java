package client.scenes;

import org.apache.commons.io.FileUtils;

import com.google.inject.Inject;

import client.ShoppingListFormatter;
import client.config.ConfigService;
import client.model.ShoppingItem;
import client.model.ShoppingList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;
import javafx.collections.ListChangeListener;
import java.util.ResourceBundle;

public class ShoppingListCtrl {

    @FXML private Button remindMeButton;
    @FXML private Label reminderStatusLabel;
    @FXML private Button addShoppingItemButton;
    @FXML private Button downloadShoppingListButton;
    @FXML private VBox shoppingList;
    @FXML private ScrollPane shoppingScrollPane;
    @FXML private VBox emptyShoppingList;
    @FXML private VBox notEmptyShoppingList;
    @FXML private Button resetShoppingListButton;
    @FXML private Label shoppingListTitleLabel;
    @FXML private Label ingredientsLabel;
    @FXML private Label noIngredientTitle;
    @FXML private Label noIngredientSubtitle;

    private ShoppingList shoppingListModel;
    private final ConfigService configService;

    @Inject
    public ShoppingListCtrl(ConfigService configService) {
        this.configService = configService;
    }

    /** Initialize the shopping list scene */
    @FXML
    public void initialize() {
        shoppingList.setFillWidth(true);
        shoppingList.setPadding(new Insets(10, 15, 10, 15));
        shoppingList.setSpacing(8);

        loadShoppingListModel();
        setupChangeListener();

        updateUILabels();

        refreshUI();
        scheduleExistingReminderIfAny();
    }

    private void setupChangeListener() {
        shoppingListModel.getObservableItems().addListener((ListChangeListener<ShoppingItem>) change -> {
            refreshUI();
        });
    }

    /** Loads the latest shopping list from config */
    private void loadShoppingListModel() {
        var config = configService.getConfig();
        if (config == null) {
            throw new IllegalStateException(
                "ConfigService returned null config"
            );
        }
        shoppingListModel = config.getShoppingList();
    }

    /** Refresh the shopping list UI from the model */
    public void refreshFromConfig() {
        loadShoppingListModel();

        updateUILabels();

        if (shoppingList != null) {
            refreshUI();
        }
    }

    /**
     * method that updates the labels from UI
     */
    private void updateUILabels() {
        ResourceBundle props = getTranslationsBundle();

        remindMeButton.setText(props.getString("button.reminder"));
        resetShoppingListButton.setText(props.getString("button.resetshoppinglist"));
        downloadShoppingListButton.setText(props.getString("button.download"));

        if (shoppingListTitleLabel != null) {
            shoppingListTitleLabel.setText(props.getString("label.shoppinglisttitle"));
        }

        if (ingredientsLabel != null) {
            ingredientsLabel.setText(props.getString("label.ingredientsoverview"));
        }

        if (noIngredientTitle != null) {
            noIngredientTitle.setText(props.getString("label.emptyShoppingList"));
        }

        if (noIngredientSubtitle != null) {
            noIngredientSubtitle.setText(props.getString("label.emptyShoppingListSubtitle"));
        }




        if ("No reminder set.".equals(reminderStatusLabel.getText()) || reminderStatusLabel.getText().isEmpty()) {
            reminderStatusLabel.setText(props.getString("label.reminder"));
        }

    }

    /**
     * Method that gets the translations from the resourcebundle
     * @return
     */
    private ResourceBundle getTranslationsBundle() {
        try {
            String languageCode = configService.getLanguage();
            return ResourceBundle.getBundle(
                    "client.scenes.Translations",
                    new java.util.Locale(languageCode)
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResourceBundle.getBundle("client.scenes.Translations", java.util.Locale.ENGLISH);
        }
    }

    /** Refreshes the shopping list UI to reflect the current model state */
    private void refreshUI() {
        if (shoppingList == null || emptyShoppingList == null || notEmptyShoppingList == null) {
            return;
        }

        shoppingList.getChildren().clear();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
        } else {
            emptyShoppingList.setVisible(false);
            notEmptyShoppingList.setVisible(true);
            
            // Sort items: unbought items alphabetically first, then bought items alphabetically
            shoppingListModel.getShoppingItems().stream()
                    .sorted((item1, item2) -> {
                        // First, sort by bought status (unbought items first)
                        if (item1.isBought() != item2.isBought()) {
                            return item1.isBought() ? 1 : -1;
                        }
                        String name1 = item1.getName() != null ? item1.getName() : "";
                        String name2 = item2.getName() != null ? item2.getName() : "";
                        return name1.compareToIgnoreCase(name2);
                    })
                    .forEach(item -> {
                        boolean startEditable = item.getName() == null || item.getName().isBlank();
                        shoppingList.getChildren().add(createShoppingItemRow(item, startEditable));
                    });
        }
    }

    /** Adds a new shopping item to the list with default values */
    @FXML
    private void addShoppingItem() {
        if (hasIncompleteItem()) {
            showInfo(
                    "Please finish editing the current item before adding a new one."
            );
            return;
        }

        ShoppingItem item = new ShoppingItem("", 0, "", null);
        shoppingListModel.addItem(item);
        saveShoppingList();
    }


    /** Checks if there is any incomplete item in the shopping list */
    private boolean hasIncompleteItem() {
        return shoppingListModel.getShoppingItems().stream()
                .anyMatch(item ->
                        item.getName() == null ||
                                item.getName().trim().isEmpty()
                );
    }

    /** Creates a UI row for a shopping item, dynamically generating FXML components.
     * @param item The shopping item
     * @param startEditable Whether the row should start in editable mode
     * @return The HBox representing the shopping item row
     */
    private HBox createShoppingItemRow(
        ShoppingItem item,
        boolean startEditable
    ) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        HBox.setHgrow(row, Priority.ALWAYS);

        CheckBox boughtCheckBox = new CheckBox();
        boughtCheckBox.setSelected(item.isBought());

        TextField combinedField = new TextField();
        setupCombinedField(combinedField);

        TextField nameField = new TextField();
        TextField amountField = new TextField();
        TextField unitField = new TextField();
        setupEditFields(nameField, amountField, unitField);

        Button editSaveButton = new Button();
        Button deleteButton = new Button("✖");

        updateCombinedField(item, combinedField);

        boughtCheckBox.setOnAction(e -> {
            item.setBought(boughtCheckBox.isSelected());
            updateCombinedField(item, combinedField);
            saveShoppingList();
        });

        final Runnable[] editAction = new Runnable[1];
        Runnable saveAction = createSaveAction(item, row, boughtCheckBox, combinedField,
                editSaveButton, deleteButton, nameField, amountField, unitField, editAction);
        editAction[0] = createEditAction(item, editSaveButton, deleteButton, row, boughtCheckBox,
                nameField, amountField, unitField, saveAction);

        deleteButton.setOnAction(e -> {
            shoppingListModel.removeItem(item);
            saveShoppingList();
            refreshUI();
        });

        setupEditButton(editSaveButton, editAction);

        row.getChildren().addAll(boughtCheckBox, combinedField, editSaveButton, deleteButton);

        if (startEditable) editAction[0].run();
        return row;
    }

    private void setupCombinedField(TextField field) {
        field.setEditable(false);
        field.setFocusTraversable(false);
        field.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        HBox.setHgrow(field, Priority.ALWAYS);
    }

    private void setupEditFields(TextField nameField, TextField amountField, TextField unitField) {
        ResourceBundle props = getTranslationsBundle();

        nameField.setPromptText(props.getString("label.ingredientNamePrompt"));
        amountField.setPromptText(props.getString("label.ingredientAmountPrompt"));
        unitField.setPromptText(props.getString("label.ingredientUnitPrompt"));
    }

    private Runnable createSaveAction(
        ShoppingItem item,
        HBox row,
        CheckBox boughtCheckBox,
        TextField combinedField,
        Button editSaveButton,
        Button deleteButton,
        TextField nameField,
        TextField amountField,
        TextField unitField,
        Runnable[] editAction
    ) {
        return () -> {
            if (nameField.getText().trim().isEmpty()) return;

            item.setName(nameField.getText().trim());
            try {
                item.setAmount(Double.parseDouble(amountField.getText().trim()));
            } catch (Exception ignored) {}
            item.setUnit(unitField.getText().trim());

            saveShoppingList();
            refreshUI();
            updateCombinedField(item, combinedField);

            editSaveButton.setText("✏");
            editSaveButton.setOnAction(e -> editAction[0].run());
            row.getChildren().setAll(boughtCheckBox, combinedField, editSaveButton, deleteButton);
        };
    }

    private Runnable createEditAction(
        ShoppingItem item,
        Button editSaveButton,
        Button deleteButton,
        HBox row,
        CheckBox boughtCheckBox,
        TextField nameField,
        TextField amountField,
        TextField unitField,
        Runnable saveAction
    ) {
        return () -> {
            if (item.isBought()) {
                showInfo("This item is marked as bought and cannot be edited.");
                return;
            }

            nameField.setText(item.getName());
            amountField.setText(item.getAmount() == 0 ? "" : String.valueOf(item.getAmount()));
            unitField.setText(item.getUnit());

            editSaveButton.setText("✔");
            editSaveButton.setOnAction(e -> saveAction.run());
            row.getChildren().setAll(boughtCheckBox, nameField, amountField, unitField, editSaveButton, deleteButton);
            nameField.requestFocus();

            editSaveButton.setOnAction(e -> saveAction.run());
            nameField.setOnAction(e -> saveAction.run());
            amountField.setOnAction(e -> saveAction.run());
            unitField.setOnAction(e -> saveAction.run());
        };
    }

    private void setupEditButton(Button editSaveButton, Runnable[] editAction) {
        editSaveButton.setText("✏");
        editSaveButton.setOnAction(e -> editAction[0].run());
    }

    /** Updates the combined field text and style based on the item's state.
    * If an item is bought, grey it out.
    */
    private void updateCombinedField(ShoppingItem item, TextField field) {
        String text = buildItemDisplayText(item);
        field.setText(text);
        field.setStyle(item.isBought()
                ? "-fx-text-fill: grey; -fx-background-color: transparent;"
                : "-fx-background-color: transparent;");
    }

    /**
     * Builds the display text for a shopping item, including name, amount, unit,
     * and source recipe name if there are duplicate names.
     * @param item The shopping item
     * @return The display text for the item
     */
    private String buildItemDisplayText(ShoppingItem item) {
        StringBuilder text = new StringBuilder();
        if (!item.getName().isBlank()) {
            text.append(item.getName());
        }
        if (item.getAmount() > 0) {
            text.append(" ").append(item.getAmount());
        }
        if (item.getUnit() != null && !item.getUnit().isBlank()) {
            text.append(" ").append(item.getUnit());
        }
        if (hasDuplicateName(item)) {
            if (
                    item.getSourceRecipeName() != null &&
                    !item.getSourceRecipeName().isBlank()
            ) {
                text
                    .append(" [")
                    .append(item.getSourceRecipeName())
                    .append("]");
            }
        }
        return text.toString();
    }

    /** Checks if there are multiple items with the same name in the shopping list.
     * @param item The shopping item to check
     * @return true if there are duplicates, false otherwise
     */
    private boolean hasDuplicateName(ShoppingItem item) {
        long count = shoppingListModel
            .getShoppingItems()
            .stream()
            .filter(
                i -> i.getName() != null && i.getName().equals(item.getName())
            )
            .count();
        return count > 1;
    }


    /** Handles the "Remind Me" button click event.
     * Has error handling for invalid inputs and schedules the reminder if valid.
     */
    @FXML
    private void handleRemindMe() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Set Reminder");
        dialog.setHeaderText("Choose reminder date and time");

        ButtonType setButton =
                new ButtonType("Set Reminder", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(setButton, ButtonType.CANCEL);

        DatePicker datePicker = new DatePicker(java.time.LocalDate.now());

        TextField hourField = new TextField("12");
        hourField.setPromptText("HH");

        TextField minuteField = new TextField("00");
        minuteField.setPromptText("MM");

        HBox timeBox = new HBox(10, hourField, minuteField);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        // Show existing reminder if any
        Long existingMillis = configService.getConfig().getShoppingListReminder();
        if (existingMillis != null) {
            java.time.LocalDateTime existing =
                    java.time.Instant.ofEpochMilli(existingMillis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
            Label warningLabel = new Label(
                    "⚠ Warning: A reminder is already set for "
                            + existing.toLocalDate() + " at " + existing.toLocalTime()
                            + ".\nSetting a new reminder will replace it."
            );
            warningLabel.setStyle("-fx-text-fill: darkred; -fx-font-weight: bold;");
            content.getChildren().add(warningLabel);

            // pre-fill existing values of any prev reminder.
            datePicker.setValue(existing.toLocalDate());
            hourField.setText(String.valueOf(existing.getHour()));
            minuteField.setText(String.valueOf(existing.getMinute()));
        }

        content.getChildren().addAll(datePicker, timeBox);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(btn -> {
            if (btn == setButton) {
                try {
                    int hour = Integer.parseInt(hourField.getText());
                    int minute = Integer.parseInt(minuteField.getText());

                    if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
                        showError("Invalid time entered.");
                        return null;
                    }

                    java.time.LocalDateTime reminderTime =
                            java.time.LocalDateTime.of(
                                    datePicker.getValue(),
                                    java.time.LocalTime.of(hour, minute)
                            );

                    long delayMillis =
                            java.time.Duration
                                    .between(java.time.LocalDateTime.now(), reminderTime)
                                    .toMillis();

                    if (delayMillis <= 0) {
                        showError("Reminder must be set in the future.");
                        return null;
                    }

                    configService.getConfig().setShoppingListReminder(
                        reminderTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
                    configService.save();

                    reminderStatusLabel.setText(
                            "Reminder set for " + reminderTime.toLocalDate()
                                    + " at " + reminderTime.toLocalTime()
                    );

                    scheduleReminder(delayMillis);

                } catch (NumberFormatException e) {
                    showError("Please enter valid numbers.");
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    /** Schedules an existing reminder if one exists. */
    private void scheduleExistingReminderIfAny() {
        Long millis = configService.getConfig().getShoppingListReminder();
        if (millis != null && millis > System.currentTimeMillis()) {
            scheduleReminder(millis - System.currentTimeMillis());
        }
    }

    /** Handles downloading the shopping list */
    @FXML
    void downloadShoppingList() {
        Window window = shoppingList.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save shopping list");
        fileChooser.setInitialFileName("shopping_list.txt");

        java.io.File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try {
                String text = (
                        new ShoppingListFormatter(shoppingListModel)
                ).formatPlaintext();
                FileUtils.writeStringToFile(file, text);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Schedules a reminder to show after the specified delay.
     * @param delayMillis Delay in milliseconds before showing the reminder
     */
    private void scheduleReminder(long delayMillis) {
        new Thread(() -> {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException ignored) {}

            StringBuilder message = new StringBuilder(
                    "Reminder to purchase shopping list!\n\nItems to buy:\n"
            );

            shoppingListModel.getShoppingItems().stream()
                    .filter(item -> !item.isBought())
                    .forEach(item ->
                            message.append("• ")
                                    .append(item.getName())
                                    .append(item.getAmount() > 0 ? " " + item.getAmount() : "")
                                    .append(item.getUnit() != null &&
                                     !item.getUnit().isBlank() ? " " + item.getUnit() : "")
                                    .append("\n")
            );

            if (message.toString().endsWith("Items to buy:\n")) {
                message.append("All items have been bought! Hooray! :D");
            }

            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Shopping Reminder");
                alert.setHeaderText(null);
                alert.setContentText(message.toString());
                alert.showAndWait();

                reminderStatusLabel.setText("No reminder set.");
                configService.getConfig().setShoppingListReminder(null);
                configService.save();
            });
        }).start();
    }

    /** Saves the current shopping list model to the config service */
    private void saveShoppingList() {
        configService.getConfig().setShoppingList(shoppingListModel);
        configService.save();
    }

    /** Displays an error alert with the specified message.
     * @param msg The error message to display
     */
    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    /** Displays an information alert with the specified message.
     * @param msg The information message to display
     */
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    /** Resets the shopping list by clearing all items */
    @FXML
    void resetShoppingList() {
        shoppingListModel.clear();
        saveShoppingList();
        refreshUI();
    }

    /** For testing: gets the shopping list model used by this controller. */
    ShoppingList getShoppingListModelForTest() {
        return this.shoppingListModel;
    }

    /** For testing: sets the shopping list model used by this controller. */
    void setShoppingListModelForTest(ShoppingList shoppingList) {
        this.shoppingListModel = shoppingList;
    }
}
