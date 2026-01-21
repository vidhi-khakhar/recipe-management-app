package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MainTest {

    @Test
    public void testMainClassExists() {
        assertNotNull(Main.class);
    }

    @Test
    public void testMainExtendsApplication() {
        assertTrue(
            Main.class.getSuperclass()
                .getName()
                .equals("javafx.application.Application")
        );
    }
}
