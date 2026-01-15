package client.scenes;

import client.ShoppingListFormatter;
import client.config.ConfigService;
import client.model.ShoppingItem;
import client.model.ShoppingList;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.commons.io.FileUtils;

public class ShoppingListCtrl {

    @FXML
    private Button addShoppingItemButton;

    @FXML
    private Button downloadShoppingListButton;

    @FXML
    private AnchorPane emptyShoppingList;

    @FXML
    private Label noIngredientSubtitle;

    @FXML
    private Label noIngredientTitle;

    @FXML
    private AnchorPane notEmptyShoppingList;

    @FXML
    private Button resetShoppingListButton;

    @FXML
    private VBox shoppingList;

    @FXML
    private ScrollPane shoppingScrollPane;

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

        loadShoppingListModel();
        refreshUI();
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

    /** Refreshes the shopping list data and UI - call this when window is opened */
    public void refreshFromConfig() {
        loadShoppingListModel();
        if (shoppingList != null) {
            // Check if UI is initialized
            refreshUI();
        }
    }

    /** Rebuilds the shopping list UI from the model */
    private void refreshUI() {
        shoppingList.getChildren().clear();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
        } else {
            emptyShoppingList.setVisible(false);
            notEmptyShoppingList.setVisible(true);

            for (ShoppingItem item : shoppingListModel.getShoppingItems()) {
                shoppingList
                    .getChildren()
                    .add(createShoppingItemRow(item, false));
            }
        }
    }

    /** Handles adding a new shopping item */
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

    /** Creates a shopping item row (HBox) and sets up delete/edit functionality.
     * @param item the ShoppingItem model
     * @param startEditable whether to start in edit mode
     * @return the constructed HBox row
     */
    private HBox createShoppingItemRow(
        ShoppingItem item,
        boolean startEditable
    ) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(row, Priority.ALWAYS);

        Label combinedLabel = new Label();
        combinedLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(combinedLabel, Priority.ALWAYS);

        TextField nameField = new TextField(item.getName());
        nameField.setPromptText("Shopping item");

        TextField amountField = new TextField(
            item.getAmount() == 0 ? "" : String.valueOf(item.getAmount())
        );
        amountField.setPromptText("Quantity");

        TextField unitField = new TextField(
            item.getUnit() == null ? "" : item.getUnit()
        );
        unitField.setPromptText("Unit");

        Button editSaveButton = new Button();
        editSaveButton.setMinWidth(50);

        Button deleteButton = new Button("✖");
        deleteButton.setMinWidth(50);
        deleteButton.setOnAction(e -> handleDeleteItem(item, row));

        setupEditSaveBehavior(
            row,
            item,
            combinedLabel,
            nameField,
            amountField,
            unitField,
            editSaveButton,
            deleteButton,
            startEditable
        );

        return row;
    }

    /** Handles deleting a shopping item
     * @param item the ShoppingItem to delete
     * @param row the HBox row representing the item
     */
    private void handleDeleteItem(ShoppingItem item, HBox row) {
        shoppingListModel.removeItem(item);
        shoppingList.getChildren().remove(row);
        saveShoppingList();

        if (shoppingListModel.getShoppingItems().isEmpty()) {
            emptyShoppingList.setVisible(true);
            notEmptyShoppingList.setVisible(false);
        } else {
            refreshUI();
        }
    }

    /** Sets up the edit/save toggle behavior for a shopping item row
     * @param row the HBox row
     * @param item the ShoppingItem model
     * @param combinedLabel the Label showing combined item info
     * @param nameField the TextField for item name
     *  @param amountField the TextField for item amount
     * @param unitField the TextField for item unit
     * @param editSaveButton the Button to toggle edit/save
     * @param deleteButton the Button to delete the item
     * @param startEditable whether to start in edit mode
     */
    private void setupEditSaveBehavior(
        HBox row,
        ShoppingItem item,
        Label combinedLabel,
        TextField nameField,
        TextField amountField,
        TextField unitField,
        Button editSaveButton,
        Button deleteButton,
        boolean startEditable
    ) {
        Runnable updateLabel = () -> {
            String text = buildItemDisplayText(item);
            combinedLabel.setText(text);
        };

        updateLabel.run();

        final Runnable[] switchToEdit = new Runnable[1];
        final Runnable[] saveItem = new Runnable[1];

        saveItem[0] = () -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                return;
            }

            double amount = item.getAmount();
            String amountText = amountField.getText().trim();
            if (!amountText.isEmpty()) {
                try {
                    amount = Double.parseDouble(amountText);
                } catch (NumberFormatException ignored) {}
            }

            String unit = unitField.getText().trim();

            item.setName(name);
            item.setAmount(amount);
            item.setUnit(unit);

            updateLabel.run();
            saveShoppingList();

            editSaveButton.setText("✏");
            row
                .getChildren()
                .setAll(combinedLabel, editSaveButton, deleteButton);
            editSaveButton.setOnAction(e -> switchToEdit[0].run());
        };

        switchToEdit[0] = () -> {
            nameField.setText(item.getName());
            amountField.setText(
                item.getAmount() == 0 ? "" : String.valueOf(item.getAmount())
            );
            unitField.setText(item.getUnit());

            editSaveButton.setText("✔");
            row
                .getChildren()
                .setAll(
                    nameField,
                    amountField,
                    unitField,
                    editSaveButton,
                    deleteButton
                );
            nameField.requestFocus();

            editSaveButton.setOnAction(e -> saveItem[0].run());
            nameField.setOnAction(e -> saveItem[0].run());
            amountField.setOnAction(e -> saveItem[0].run());
            unitField.setOnAction(e -> saveItem[0].run());
        };

        if (startEditable) {
            switchToEdit[0].run();
        } else {
            editSaveButton.setText("✏");
            editSaveButton.setOnAction(e -> switchToEdit[0].run());
            row
                .getChildren()
                .setAll(combinedLabel, editSaveButton, deleteButton);
        }
    }

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

    /** Saves the shopping list to config */
    private void saveShoppingList() {
        try {
            configService.getConfig().setShoppingList(shoppingListModel);
            configService.save();
        } catch (Exception e) {
            e.printStackTrace();
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

    /** Resets the shopping list */
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

    ShoppingList getShoppingListModelForTest() {
        return this.shoppingListModel;
    }
}
