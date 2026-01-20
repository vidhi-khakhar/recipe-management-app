package client.scenes;
import commons.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class AddRecipeIngredientCtrlTest {

    private AddRecipeIngredientCtrl controller;

    private ChoiceBox<Unit> unitChoiceBox;
    private Label infoLabel;

    private Recipe recipe;
    private Ingredient sugar;
    private Ingredient flour;

    /**
     * Without this, JavaFX screams this: IllegalStateException: Toolkit not initialized
     * So, this starts the JavaFX application thread once, before each test,
     * but it does not launch a window.
     */
    @BeforeAll
    static void startJavaFx() {
        Platform.startup(() -> {});
    }

    @BeforeEach
    void setup() {
        controller = new AddRecipeIngredientCtrl(null);

        unitChoiceBox = new ChoiceBox<>();
        infoLabel = new Label();

        inject("unitChoiceBox", unitChoiceBox);
        inject("infoLabel", infoLabel);

        sugar = new Ingredient("Zahar", 1, 1,1 );
        flour = new Ingredient("Faina", 1,1 , 1);

        recipe = new Recipe("Bors");
        controller.setRecipe(recipe);
    }


    @Test
    void duplicateIngredient() {
        recipe.getIngredients().add(
                RecipeIngredient.formal(recipe, sugar, 67.9, Unit.G)
        );

        assertTrue(controller.duplicateIngredient(recipe, sugar));
    }

    @Test
    void notDuplicateIngredient() {
        assertFalse(controller.duplicateIngredient(recipe, flour));
    }


    @Test
    void validFormalAmount() {
        assertTrue(controller.validateFormalAmount("2.5"));
    }


    @Test
    void updateUnitBoxFormal() {
        controller.updateUnitChoiceBoxVisibility("Formal");

        assertTrue(unitChoiceBox.isVisible());
        assertFalse(infoLabel.isVisible());
    }

    @Test
    void updateUnitBoxInformal() {
        controller.updateUnitChoiceBoxVisibility("Informal");

        assertFalse(unitChoiceBox.isVisible());
        assertTrue(infoLabel.isVisible());
    }

    @Test
    void updateUnitBox() {
        assertDoesNotThrow(() -> controller.updateUnitChoiceBoxVisibility(null));
    }

    /**
     * To manually fill in the fields FXML would inject.
     * @param text the name of the field
     * @param value the object to assign
     */
    private void inject(String text, Object value) {
        try {
            Field field = AddRecipeIngredientCtrl.class.getDeclaredField(text);
            field.setAccessible(true);
            field.set(controller, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
