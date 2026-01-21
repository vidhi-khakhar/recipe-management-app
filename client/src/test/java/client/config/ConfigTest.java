package client.config;

import static org.junit.jupiter.api.Assertions.*;

import client.model.ShoppingList;
import java.util.ArrayList;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class ConfigTest {

    @Test
    public void testDefaultConstructor() {
        Config config = new Config();

        assertNull(config.getServerUrl());
        assertNull(config.getFavoriteRecipeIds());
        assertNull(config.getLanguage());
        assertNotNull(config.getShoppingList());
        assertNull(config.getShoppingListReminder());
    }

    @Test
    public void testSettersAndGetters() {
        Config config = new Config();

        config.setServerUrl("http://localhost:8080");
        config.setFavoriteRecipeIds(new ArrayList<>());
        config.setLanguage(Locale.ENGLISH);
        config.setShoppingListReminder(100L);

        assertEquals("http://localhost:8080", config.getServerUrl());
        assertNotNull(config.getFavoriteRecipeIds());
        assertEquals(Locale.ENGLISH, config.getLanguage());
        assertEquals(100L, config.getShoppingListReminder());
    }

    @Test
    public void testGetShoppingListWithNull() {
        Config config = new Config();

        ShoppingList shoppingList = config.getShoppingList();

        assertNotNull(shoppingList);
    }

    @Test
    public void testGetShoppingListWithExisting() {
        Config config = new Config();
        ShoppingList shoppingList = new ShoppingList();
        config.setShoppingList(shoppingList);

        assertEquals(shoppingList, config.getShoppingList());
    }
}
