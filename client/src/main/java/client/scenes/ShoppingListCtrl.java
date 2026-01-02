package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class ShoppingListCtrl {

    @FXML
    private Button addShoppingItemButton;

    @FXML
    private Button downloadShoppingListButton;

    @FXML
    private Button resetShoppingListButton;

    @FXML
    private VBox shoppingList;

    @FXML
    private ScrollPane shoppingScrollPane;

    @FXML
    void addShoppingItem() {
        // This method is directly linked to the addShoppingItemButton
        System.out.println("Add shopping item button clicked");
    }

    @FXML
    void downloadShoppingList() {
        // This method is directly linked to the downloadShoppingListButton
        System.out.println("Download shopping list button clicked");
    }

    @FXML
    void resetShoppingList() {
        // This method is directly linked to the resetShoppingListButton
        System.out.println("Reset shopping list button clicked");
    }

}