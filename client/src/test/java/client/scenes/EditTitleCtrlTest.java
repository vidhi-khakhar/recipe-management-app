package client.scenes;

import static org.junit.jupiter.api.Assertions.*;

import client.utils.RecipeServerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EditTitleCtrlTest {

    @Mock
    private MainCtrl mockMainCtrl;

    @Mock
    private RecipeServerUtils mockRecipeServerUtils;

    private EditTitleCtrl editTitleCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        editTitleCtrl = new EditTitleCtrl(mockMainCtrl, mockRecipeServerUtils);
    }

    @Test
    public void testConstructor() {
        EditTitleCtrl ctrl = new EditTitleCtrl(
            mockMainCtrl,
            mockRecipeServerUtils
        );
        assertNotNull(ctrl);
    }

    @Test
    public void testEditTitle() {
        assertNotNull(editTitleCtrl);
    }
}
