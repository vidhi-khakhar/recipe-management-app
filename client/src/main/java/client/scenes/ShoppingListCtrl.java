package client.scenes;

import com.google.inject.Inject;

import client.config.ConfigService;
import client.model.ShoppingItem;
import client.model.ShoppingList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ShoppingListCtrl {

    @FXML
    private Button addShoppingItemButton;

    @FXML
    private Button downloadShoppingListButton;

    @FXML
    private VBox shoppingList;

    @FXML
    private ScrollPane shoppingScrollPane;

    @FXML
    private javafx.scene.layout.AnchorPane emptyShoppingList;

    @FXML
    private javafx.scene.layout.AnchorPane notEmptyShoppingList;

    @FXML
    private Button resetShoppingListButton;

    private ShoppingList shoppingListModel;
    private ConfigService configService;

    @Inject
    public ShoppingListCtrl(ConfigService configService) {
        this.configService = configService;
    }

    /** Initializes the shopping list UI */
    @FXML
    public void initialize() {
        shoppingList.setFillWidth(true);
        shoppingList.setPadding(new Insets(10, 15, 10, 15));
        shoppingList.setSpacing(8);

        var config = configService.getConfig();
        if (config == null) {
            throw new IllegalStateException("ConfigService returned null config");
        }
        shoppingListModel = config.getShoppingList();

        refreshUI();
    }

    private void refreshUI() {
        shoppingList.getChildren().clear();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
        } else {
            emptyShoppingList.setVisible(false);
            notEmptyShoppingList.setVisible(true);

            for (ShoppingItem item : shoppingListModel.getShoppingItems()) {
                shoppingList.getChildren().add(createShoppingItemRow(item, false));
            }
        }
    }

    @FXML
    private void addShoppingItem() {
        ShoppingItem item = new ShoppingItem("", 0, "", null);
        shoppingListModel.addItem(item);
        saveShoppingList();

        HBox row = createShoppingItemRow(item, true);
        shoppingList.getChildren().add(row);

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

        // Display field for combined text (non-editable to match editing size)
        TextField combinedField = new TextField();
        combinedField.setEditable(false);
        combinedField.setFocusTraversable(false);
        combinedField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        HBox.setHgrow(combinedField, Priority.ALWAYS);

        // Text fields with prompt texts
        TextField nameField = new TextField(item.getName());
        nameField.setPromptText("Item name");
        HBox.setHgrow(nameField, Priority.ALWAYS);

        TextField amountField = new TextField(item.getAmount() == 0 ? "" : String.valueOf(item.getAmount()));
        amountField.setPromptText("Quantity");
        HBox.setHgrow(amountField, Priority.ALWAYS);

        TextField unitField = new TextField(item.getUnit() == null ? "" : item.getUnit());
        unitField.setPromptText("Units");
        HBox.setHgrow(unitField, Priority.ALWAYS);

        Button editSaveButton = new Button();
        editSaveButton.setMinWidth(50);

        Button deleteButton = new Button("✖");
        deleteButton.setMinWidth(50);
        deleteButton.setOnAction(e -> handleDeleteItem(item, row));

        setupEditSaveBehavior(
                row,
                item,
                combinedField,
                nameField,
                amountField,
                unitField,
                editSaveButton,
                deleteButton,
                startEditable,
                boughtCheckBox
        );

        return row;
    }

    private void handleDeleteItem(ShoppingItem item, HBox row) {
        shoppingListModel.removeItem(item);
        shoppingList.getChildren().remove(row);
        saveShoppingList();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
        }
    }

    private void setupEditSaveBehavior(
            HBox row,
            ShoppingItem item,
            TextField combinedField,
            TextField nameField,
            TextField amountField,
            TextField unitField,
            Button editSaveButton,
            Button deleteButton,
            boolean startEditable,
            CheckBox boughtCheckBox
    ) {
        updateCombinedField(item, combinedField);

        boughtCheckBox.setOnAction(e -> {
            item.setBought(boughtCheckBox.isSelected());
            updateCombinedField(item, combinedField);
            saveShoppingList();
        });

        final Runnable[] switchToEdit = new Runnable[1];

        Runnable saveItem = createSaveAction(
                row,
                item,
                combinedField,
                nameField,
                amountField,
                unitField,
                editSaveButton,
                deleteButton,
                boughtCheckBox,
                () -> switchToEdit[0].run()
        );

        switchToEdit[0] = () -> {
            if (item.isBought()) {
                javafx.scene.control.Alert alert =
                        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Cannot Edit Item");
                alert.setHeaderText(null);
                alert.setContentText("This item has been marked as bought and cannot be edited.");
                alert.showAndWait();
                return;
            }

            nameField.setText(item.getName());
            amountField.setText(item.getAmount() == 0 ? "" : String.valueOf(item.getAmount()));
            unitField.setText(item.getUnit());

            editSaveButton.setText("✔");
            row.getChildren().setAll(
                    boughtCheckBox,
                    nameField,
                    amountField,
                    unitField,
                    editSaveButton,
                    deleteButton
            );

            nameField.requestFocus();

            editSaveButton.setOnAction(e -> saveItem.run());
            nameField.setOnAction(e -> saveItem.run());
            amountField.setOnAction(e -> saveItem.run());
            unitField.setOnAction(e -> saveItem.run());
        };

        if (startEditable) {
            switchToEdit[0].run();
        } else {
            editSaveButton.setText("✏");
            editSaveButton.setOnAction(e -> switchToEdit[0].run());

            row.getChildren().setAll(
                    boughtCheckBox,
                    combinedField,
                    editSaveButton,
                    deleteButton
            );
        }
    }

    private void updateCombinedField(ShoppingItem item, TextField combinedField) {
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

        combinedField.setText(text.toString());

        combinedField.setStyle(
                item.isBought()
                        ? "-fx-text-fill: grey; -fx-background-color: transparent; -fx-border-color: transparent;"
                        : "-fx-background-color: transparent; -fx-border-color: transparent;"
        );
    }

    private Runnable createSaveAction(HBox row, ShoppingItem item, TextField combinedField,
                                     TextField nameField, TextField amountField, TextField unitField,
                                     Button editSaveButton, Button deleteButton, CheckBox boughtCheckBox,
                                     Runnable switchToEdit) {
        return () -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;

            double amount = item.getAmount();
            String amountText = amountField.getText().trim();
            if (!amountText.isEmpty()) {
                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException ignored) {}
            }

            item.setName(name);
            item.setAmount(amount);
            item.setUnit(unitField.getText().trim());

            updateCombinedField(item, combinedField);
            saveShoppingList();

            editSaveButton.setText("✏");
            row.getChildren().setAll(
                    boughtCheckBox,
                    combinedField,
                    editSaveButton,
                    deleteButton
            );

            editSaveButton.setOnAction(e -> switchToEdit.run());
        };
    }

    private void saveShoppingList() {
        try {
            configService.getConfig().setShoppingList(shoppingListModel);
            configService.save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void downloadShoppingList() {
        System.out.println("Download shopping list button clicked");
    }

    @FXML
    void resetShoppingList() {
        shoppingListModel.clear();
        shoppingList.getChildren().clear();
        saveShoppingList();

        emptyShoppingList.setVisible(true);
        notEmptyShoppingList.setVisible(false);
    }

    void setShoppingListModelForTest(ShoppingList shoppingList) {
        this.shoppingListModel = shoppingList;
    }
}
