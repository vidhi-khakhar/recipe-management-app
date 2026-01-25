package client;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MyFXMLTest {

    @Test
    public void testConstructor() throws Exception {
        com.google.inject.Injector mockInjector = null;
        MyFXML fxml = new MyFXML(mockInjector);

        assertNotNull(fxml);
    }

    @Test
    public void testMyFXMLClassExists() {
        assertNotNull(MyFXML.class);
    }
}
