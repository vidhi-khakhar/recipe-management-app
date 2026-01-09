package client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class ShoppingItemTest {

    @Test
    void defaultConstructorTest() {
        ShoppingItem item = new ShoppingItem();

        assertNull(item.getName());
        assertEquals(0.0, item.getAmount());
        assertNull(item.getUnit());
        assertNull(item.getSourceRecipeName());
    }

    @Test
    void nameOnlyConstructorTest() {
        ShoppingItem item = new ShoppingItem("Milk");

        assertEquals("Milk", item.getName());

        // Check all of the default values.
        assertEquals(0.0, item.getAmount());
        assertEquals("", item.getUnit());
        assertNull(item.getSourceRecipeName());
    }

    @Test
    void fullConstructorTest() {
        ShoppingItem item = new ShoppingItem("Sugar", 500, "g", "Cake");

        assertEquals("Sugar", item.getName());
        assertEquals(500, item.getAmount());
        assertEquals("g", item.getUnit());
        assertEquals("Cake", item.getSourceRecipeName());
    }


    @Test
    void setUnitNullTest() {
        ShoppingItem item = new ShoppingItem("Salt");

        item.setUnit(null);

        assertEquals("", item.getUnit());
    }

    @Test
    void settersAndGettersTest() {
        ShoppingItem item = new ShoppingItem();

        item.setName("Eggs");
        item.setAmount(12);
        item.setUnit("pcs");
        item.setSourceRecipeName("Omelette");

        assertEquals("Eggs", item.getName());
        assertEquals(12, item.getAmount());
        assertEquals("pcs", item.getUnit());
        assertEquals("Omelette", item.getSourceRecipeName());
    }
}
