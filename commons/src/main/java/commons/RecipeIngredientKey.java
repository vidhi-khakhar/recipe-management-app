package commons;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeIngredientKey implements Serializable {

    private Long recipeId;
    private Long ingredientId;

    @SuppressWarnings("unused")
    public RecipeIngredientKey() {
    }

    public RecipeIngredientKey(Long recipeId, Long ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }

    /**
     * Equality is based on the composite key (recipeId + ingredientId).
     * @param o the other object to compare
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredientKey that = (RecipeIngredientKey) o;
        return Objects.equals(recipeId, that.recipeId) && Objects.equals(ingredientId, that.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, ingredientId);
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public Long getIngredientId() {
        return ingredientId;
    }
}
