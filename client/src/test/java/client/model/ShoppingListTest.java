package client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ShoppingListTest {

    @Test
    void newListTest() {
        ShoppingList list = new ShoppingList();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addItemTest() {
        ShoppingList list = new ShoppingList();
        ShoppingItem item = new ShoppingItem("Milk");

        list.addItem(item);

        assertEquals(1, list.size());
        assertEquals(item, list.getShoppingItems().get(0));
    }

    @Test
    void addItemNullest() {
        ShoppingList list = new ShoppingList();

        list.addItem(null);

        assertTrue(list.isEmpty());
    }

    @Test
    void removeItemTest() {
        ShoppingList list = new ShoppingList();
        ShoppingItem milk = new ShoppingItem("Milk");
        ShoppingItem eggs = new ShoppingItem("Eggs");

        list.addItem(milk);
        list.addItem(eggs);

        list.removeItem(milk);

        assertEquals(1, list.size());
        assertEquals("Eggs", list.getShoppingItems().get(0).getName());
    }

    @Test
    void clearShoppingListTest() {
        ShoppingList list = new ShoppingList();
        list.addItem(new ShoppingItem("Milk"));
        list.addItem(new ShoppingItem("Eggs"));

        list.clear();

        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }
}
