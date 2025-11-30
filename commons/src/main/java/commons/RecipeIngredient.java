package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientKey id;

    // Many lines belong to one recipe
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonIgnore
    private Recipe recipe;

    // One ingredient can be used in many recipes
    @ManyToOne(optional = false)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;


    /**
     * Formal amount (e.g. 100). For informal amounts, leave null.
     */
    @Column(name = "amount")
    private Double amount;

    /**
     * Unit for the amount (e.g. G, TBSP). For informal amounts, leave null.
     */
    @Enumerated(EnumType.STRING)
    private Unit unit;

    /**
     * Informal description, e.g. "a pinch", "to taste".
     * If this is set, amount/unit can be null.
     */
    @Column(name = "informal_amount")
    private String informal;

    @SuppressWarnings("unused")
    protected RecipeIngredient() {}

    /**
     * Creates a RecipeIngredient with formal units
     * @param recipe the recipe where the RecipeIngredient will be
     * @param ingredient The ingredient in RecipeIngredient
     * @param amount the amount of ingredient
     * @param unit the unit of the amount
     * @return The recipe ingredient
     */
    public static RecipeIngredient formal(Recipe recipe, Ingredient ingredient,  Double amount, Unit unit) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        Objects.requireNonNull(recipe);
        Objects.requireNonNull(ingredient);

        recipeIngredient.id =  new RecipeIngredientKey(recipe.getRecipeId(), ingredient.getId());
        recipeIngredient.recipe = recipe;
        recipeIngredient.ingredient = ingredient;
        recipeIngredient.amount = amount;
        recipeIngredient.unit = unit;
        recipeIngredient.informal = null;

        return recipeIngredient;
    }

    /**
     * Creates a RecipeIngredient with informal units
     * @param recipe the recipe where the RecipeIngredient will be
     * @param ingredient The ingredient in RecipeIngredient
     * @param informal The informal amount of the ingredient
     * @return the recipe ingredient
     */
    public static RecipeIngredient informal(Recipe recipe, Ingredient ingredient, String informal) {
        RecipeIngredient recipeIngredient = new RecipeIngredient();

        recipeIngredient.id =  new RecipeIngredientKey(recipe.getRecipeId(), ingredient.getId());
        recipeIngredient.recipe = recipe;
        recipeIngredient.ingredient = ingredient;
        recipeIngredient.informal = informal;
        recipeIngredient.amount = null;
        recipeIngredient.unit = null;

        return recipeIngredient;
    }

    /** Enforce: either (amount+unit) OR informal, and not half-formal. */
    @PrePersist
    @PreUpdate
    private void validate() {
        boolean hasFormal = amount != null || unit != null;
        boolean hasInformal = informal != null && !informal.isBlank();

        if ((amount == null) != (unit == null)) {
            throw new IllegalStateException("Formal requires both amount and unit.");
        }

        if (hasFormal == hasInformal) {
            throw new IllegalStateException("Must set either formal (amount+unit) OR informal, but not both.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient that = (RecipeIngredient) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Getters/setters

    public RecipeIngredientKey getId() { return id; }

    public Recipe getRecipe() { return recipe; }

    public Ingredient getIngredient() { return ingredient; }

    public Double getAmount() { return amount; }

    /**
     * Since informal and formal units can not happen at the same time if an amount is set then the informal becomes null
     */
    public void setFormal(Double amount, Unit unit) {
        this.amount = amount;
        this.unit = unit;
        this.informal = null;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getInformal() { return informal; }

    /**
     * Since informal and formal units can not happen at the same time if an informal amount is set then the amount and unit becomes null
     */
    public void setInformal(String informal) {
        this.informal = informal;
        this.unit = null;
        this.amount = null;
    }
}


