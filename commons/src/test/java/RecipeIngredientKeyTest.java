package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeIngredientKeyTest {

    @Test
    void testConstructorWithValidValues() {
        Long recipeId = 1L;
        Long ingredientId = 2L;

        RecipeIngredientKey key = new RecipeIngredientKey(recipeId, ingredientId);

        assertEquals(recipeId, key.getRecipeId());
        assertEquals(ingredientId, key.getIngredientId());
    }

    @Test
    void testSetRecipeId() {
        RecipeIngredientKey key = new RecipeIngredientKey();
        key.setRecipeId(10L);

        assertEquals(10L, key.getRecipeId());
    }

    @Test
    void testSetIngredientId() {
        RecipeIngredientKey key = new RecipeIngredientKey();
        key.setIngredientId(20L);

        assertEquals(20L, key.getIngredientId());
    }

    @Test
    void testEqualsSameObject() {
        RecipeIngredientKey key = new RecipeIngredientKey(1L, 2L);

        assertTrue(key.equals(key));
    }

    @Test
    void testEqualsSameValues() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(1L, 2L);

        assertEquals(key1, key2);
    }

    @Test
    void testEqualsDifferentRecipeId() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(99L, 2L);

        assertNotEquals(key1, key2);
    }

    @Test
    void testEqualsDifferentIngredientId() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(1L, 99L);

        assertNotEquals(key1, key2);
    }

    @Test
    void testEqualsWithNull() {
        RecipeIngredientKey key = new RecipeIngredientKey(1L, 2L);

        assertFalse(key.equals(null));
    }

    @Test
    void testEqualsDifferentClass() {
        RecipeIngredientKey key = new RecipeIngredientKey(1L, 2L);

        assertFalse(key.equals(new Object()));
    }

    @Test
    void testHashCodeConsistency() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(1L, 2L);

        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testHashCodeDifferentValues() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(3L, 4L);

        assertNotEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testHashCodeNullValues() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(null, null);
        RecipeIngredientKey key2 = new RecipeIngredientKey(null, null);

        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void testEqualsBothNull() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(null, null);
        RecipeIngredientKey key2 = new RecipeIngredientKey(null, null);

        assertEquals(key1, key2);
    }

    @Test
    void testEqualsOneNullRecipeId() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(null, 2L);
        RecipeIngredientKey key2 = new RecipeIngredientKey(1L, 2L);

        assertNotEquals(key1, key2);
    }

    @Test
    void testEqualsOneNullIngredientId() {
        RecipeIngredientKey key1 = new RecipeIngredientKey(1L, null);
        RecipeIngredientKey key2 = new RecipeIngredientKey(1L, 2L);

        assertNotEquals(key1, key2);
    }
}
