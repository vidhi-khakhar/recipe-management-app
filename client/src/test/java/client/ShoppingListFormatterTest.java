package client;

import static org.junit.jupiter.api.Assertions.*;

import client.model.ShoppingItem;
import client.model.ShoppingList;
import org.junit.jupiter.api.Test;

public class ShoppingListFormatterTest {

    @Test
    public void testFormatPlaintextWithEmptyList() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        String result = formatter.formatPlaintext();

        assertTrue(result.contains("No items in shopping list"));
    }

    @Test
    public void testFormatPlaintextWithItems() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingItem item = new ShoppingItem("Sugar", 100, "g", "Cake");
        shoppingList.addItem(item);

        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        String result = formatter.formatPlaintext();

        assertTrue(result.contains("Shopping List"));
        assertTrue(result.contains("Sugar"));
    }

    @Test
    public void testFormatMarkdownWithEmptyList() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        String result = formatter.formatMarkdown();

        assertTrue(result.contains("No items in shopping list"));
    }

    @Test
    public void testFormatMarkdownWithItems() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingItem item = new ShoppingItem("Sugar", 100, "g", "Cake");
        shoppingList.addItem(item);

        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        String result = formatter.formatMarkdown();

        assertTrue(result.contains("Shopping List"));
        assertTrue(result.contains("Sugar"));
        assertTrue(result.contains("|"));
    }

    @Test
    public void testFormatMarkdownWithNullSourceRecipe() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingItem item = new ShoppingItem();
        item.setName("Salt");
        item.setAmount(0);
        item.setUnit("to taste");
        shoppingList.addItem(item);

        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        String result = formatter.formatMarkdown();

        assertTrue(result.contains("-"));
    }

    @Test
    public void testConstructor() {
        ShoppingList shoppingList = new ShoppingList();
        ShoppingListFormatter formatter = new ShoppingListFormatter(
            shoppingList
        );

        assertNotNull(formatter);
    }
}
