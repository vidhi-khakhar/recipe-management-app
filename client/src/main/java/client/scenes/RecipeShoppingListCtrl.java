package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class RecipeShoppingListCtrl {

    @FXML
    private Button addShoppingItemButton;

    @FXML
    private Button addToShoppingListButton;

    @FXML
    private VBox shoppingList;

    @FXML
    private ScrollPane shoppingScrollPane;

    @FXML
    void addShoppingItem(ActionEvent event) {
        System.out.println("Add shopping item button clicked");
    }

    @FXML
    void addToShoppingList(ActionEvent event) {
        System.out.println("Add to shopping list button clicked");
    }

}
