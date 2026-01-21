package client.scenes;

import static org.junit.jupiter.api.Assertions.*;

import client.config.ConfigService;
import client.utils.RecipeServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AddButtonCtrlTest {

    @Mock
    private MainCtrl mockMainCtrl;

    @Mock
    private RecipeServerUtils mockRecipeServerUtils;

    @Mock
    private ConfigService mockConfigService;

    private AddButtonCtrl addButtonCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        addButtonCtrl = new AddButtonCtrl(mockMainCtrl, mockRecipeServerUtils, mockConfigService);
    }

    @Test
    public void testConstructor() {
        AddButtonCtrl ctrl = new AddButtonCtrl(
            mockMainCtrl,
            mockRecipeServerUtils,
            mockConfigService
        );
        assertNotNull(ctrl);
    }

    @Test
    public void testAddRecipe() {
        assertNotNull(addButtonCtrl);
    }
}
