package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.config.ConfigService;
import client.model.ShoppingItem;
import client.model.ShoppingList;

class ShoppingListCtrlTest {

    private ShoppingListCtrl controller;
    private ShoppingList shoppingList;

    @BeforeEach
    void setUp() {
        // Ensure clean config state
        shoppingList = new ShoppingList();
        ConfigService.getConfig().setShoppingList(shoppingList);

        controller = new ShoppingListCtrl();
        controller.setShoppingListModelForTest(shoppingList);
    }

    @Test
    void addShoppingItemTest() {
        assertEquals(0, shoppingList.getShoppingItems().size());

        ShoppingItem item = new ShoppingItem("Milk", 2, "L", null);
        shoppingList.addItem(item);

        assertEquals(1, shoppingList.getShoppingItems().size());
        assertEquals("Milk", shoppingList.getShoppingItems().get(0).getName());
    }

    @Test
    void removeShoppingItemTest() {
        ShoppingItem item = new ShoppingItem("Eggs", 12, "pcs", null);
        shoppingList.addItem(item);

        assertEquals(1, shoppingList.getShoppingItems().size());

        shoppingList.removeItem(item);

        assertTrue(shoppingList.getShoppingItems().isEmpty());
    }

    @Test
    void resetShoppingListTest() {
        shoppingList.addItem(new ShoppingItem("Bread", 1, "", null));
        shoppingList.addItem(new ShoppingItem("Butter", 1, "", null));

        assertEquals(2, shoppingList.getShoppingItems().size());

        shoppingList.clear();

        assertTrue(shoppingList.getShoppingItems().isEmpty());
    }

    @Test
    void editingItemTest() {
        ShoppingItem item = new ShoppingItem("Sugar", 1, "kg", null);
        shoppingList.addItem(item);

        item.setName("Brown Sugar");
        item.setAmount(2);
        item.setUnit("kg");

        assertEquals("Brown Sugar", item.getName());
        assertEquals(2, item.getAmount());
        assertEquals("kg", item.getUnit());
    }

    // Testing a previous bug where changing the name reset the amount
    @Test
    void amount_isNotReset_whenNameChanges() {
        ShoppingItem item = new ShoppingItem("Flour", 3, "kg", null);
        shoppingList.addItem(item);

        item.setName("Whole Wheat Flour");

        assertEquals(3, item.getAmount());
    }

    @Test
    void refreshFromConfig_updatesShoppingList() {
        assertEquals(0, controller.getShoppingListModelForTest().size());

        ShoppingItem item1 = new ShoppingItem("1", 2, "L", "Recipe1");
        ShoppingItem item2 = new ShoppingItem("2", 12, "pcs", "Recipe1");
        
        ConfigService.getConfig().getShoppingList().addItem(item1);
        ConfigService.getConfig().getShoppingList().addItem(item2);

        controller.refreshFromConfig();

        assertEquals(2, controller.getShoppingListModelForTest().size());
        
        assertEquals("1", controller.getShoppingListModelForTest().getShoppingItems().get(0).getName());
        assertEquals("2", controller.getShoppingListModelForTest().getShoppingItems().get(1).getName());
    }
}
