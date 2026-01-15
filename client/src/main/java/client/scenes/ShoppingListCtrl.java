package client.scenes;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.model.ShoppingItem;
import client.model.ShoppingList;
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

public class ShoppingListCtrl {

    @FXML private Button remindMeButton;
    @FXML private Label reminderStatusLabel;
    @FXML private Button addShoppingItemButton;
    @FXML private Button downloadShoppingListButton;
    @FXML private VBox shoppingList;
    @FXML private ScrollPane shoppingScrollPane;
    @FXML private javafx.scene.layout.AnchorPane emptyShoppingList;
    @FXML private javafx.scene.layout.AnchorPane notEmptyShoppingList;
    @FXML private Button resetShoppingListButton;

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
        refreshUI();
        scheduleExistingReminderIfAny();
    }

    private void loadShoppingListModel() {
        shoppingListModel = configService.getConfig().getShoppingList();
    }

    public void refreshFromConfig() {
        loadShoppingListModel();
        refreshUI();
    }


    private void refreshUI() {
        if (shoppingList == null || emptyShoppingList == null || notEmptyShoppingList == null) {
            return;
        }

        shoppingList.getChildren().clear();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
            return;
        }

        emptyShoppingList.setVisible(false);
        notEmptyShoppingList.setVisible(true);

        for (ShoppingItem item : shoppingListModel.getShoppingItems()) {
            shoppingList.getChildren().add(createShoppingItemRow(item, false));
        }
    }


    @FXML
    private void addShoppingItem() {
        ShoppingItem item = new ShoppingItem("", 0, "", null);
        shoppingListModel.addItem(item);
        saveShoppingList();

        shoppingList.getChildren().add(createShoppingItemRow(item, true));
        emptyShoppingList.setVisible(false);
        notEmptyShoppingList.setVisible(true);
    }

    private HBox createShoppingItemRow(ShoppingItem item, boolean startEditable) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        HBox.setHgrow(row, Priority.ALWAYS);

        CheckBox boughtCheckBox = new CheckBox();
        boughtCheckBox.setSelected(item.isBought());

        TextField combinedField = new TextField();
        combinedField.setEditable(false);
        combinedField.setFocusTraversable(false);
        combinedField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        HBox.setHgrow(combinedField, Priority.ALWAYS);

        TextField nameField = new TextField();
        nameField.setPromptText("Item name");

        TextField amountField = new TextField();
        amountField.setPromptText("Quantity");

        TextField unitField = new TextField();
        unitField.setPromptText("Units");

        Button editSaveButton = new Button();
        Button deleteButton = new Button("✖");
        deleteButton.setOnAction(e -> {
            shoppingListModel.removeItem(item);
            saveShoppingList();
            refreshUI();
        });

        updateCombinedField(item, combinedField);

        boughtCheckBox.setOnAction(e -> {
            item.setBought(boughtCheckBox.isSelected());
            updateCombinedField(item, combinedField);
            saveShoppingList();
        });

        final Runnable[] editAction = new Runnable[1];

        Runnable saveAction = () -> {
            if (nameField.getText().trim().isEmpty()) return;

            item.setName(nameField.getText().trim());
            try {
                item.setAmount(Double.parseDouble(amountField.getText().trim()));
            } catch (Exception ignored) {}
            item.setUnit(unitField.getText().trim());

            saveShoppingList();
            updateCombinedField(item, combinedField);

            editSaveButton.setText("✏");
            editSaveButton.setOnAction(e -> editAction[0].run());
            row.getChildren().setAll(
                    boughtCheckBox,
                    combinedField,
                    editSaveButton,
                    deleteButton
            );
        };

        editAction[0] = () -> {
            if (item.isBought()) {
                showInfo("This item is marked as bought and cannot be edited.");
                return;
            }

            nameField.setText(item.getName());
            amountField.setText(item.getAmount() == 0 ? "" : String.valueOf(item.getAmount()));
            unitField.setText(item.getUnit());

            editSaveButton.setText("✔");
            editSaveButton.setOnAction(e -> saveAction.run());
            row.getChildren().setAll(
                    boughtCheckBox,
                    nameField,
                    amountField,
                    unitField,
                    editSaveButton,
                    deleteButton
            );
        };


        editSaveButton.setText("✏");
        editSaveButton.setOnAction(e -> editAction[0].run());

        row.getChildren().addAll(boughtCheckBox, combinedField, editSaveButton, deleteButton);

        if (startEditable) editAction[0].run();
        return row;
    }

    private void updateCombinedField(ShoppingItem item, TextField field) {
        String text = buildItemDisplayText(item);
        field.setText(text);
        field.setStyle(item.isBought()
                ? "-fx-text-fill: grey; -fx-background-color: transparent;"
                : "-fx-background-color: transparent;");
    }

    private String buildItemDisplayText(ShoppingItem item) {
        StringBuilder text = new StringBuilder(item.getName());
        if (item.getAmount() > 0) text.append(" ").append(item.getAmount());
        if (item.getUnit() != null && !item.getUnit().isBlank()) text.append(" ").append(item.getUnit());

        if (hasDuplicateName(item) && item.getSourceRecipeName() != null) {
            text.append(" [").append(item.getSourceRecipeName()).append("]");
        }
        return text.toString();
    }

    private boolean hasDuplicateName(ShoppingItem item) {
        return shoppingListModel.getShoppingItems().stream()
                .filter(i -> i.getName().equals(item.getName()))
                .count() > 1;
    }


    
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

        // 🔹 Show existing reminder if any
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

            // pre-fill existing values
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


    private void scheduleExistingReminderIfAny() {
        Long millis = configService.getConfig().getShoppingListReminder();
        if (millis != null && millis > System.currentTimeMillis()) {
            scheduleReminder(millis - System.currentTimeMillis());
        }
    }

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


 
    private void saveShoppingList() {
        configService.getConfig().setShoppingList(shoppingListModel);
        configService.save();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }

    @FXML
    void resetShoppingList() {
        shoppingListModel.clear();
        saveShoppingList();
        refreshUI();
    }

    @FXML
    private void downloadShoppingList() {
        System.out.println("Download shopping list button pressed");
    }


    ShoppingList getShoppingListModelForTest() {
        return this.shoppingListModel;
    }

    void setShoppingListModelForTest(ShoppingList shoppingList) {
        this.shoppingListModel = shoppingList;
    }

}
