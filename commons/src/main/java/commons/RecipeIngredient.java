package commons;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_ingredient")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientKey id;

    // Many lines belong to one recipe
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    // One ingredient can be used in many recipes
    @ManyToOne(optional = false, cascade = CascadeType.MERGE)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;


    /**
     * Formal amount (e.g. 100). For informal amounts, leave null.
     */
    @Column(name = "amount")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double amount;

    /**
     * Unit for the amount (e.g. G, TBSP). For informal amounts, leave null.
     */
    @Enumerated(EnumType.STRING)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Unit unit;

    /**
     * Informal description, e.g. "a pinch", "to taste".
     * If this is set, amount/unit can be null.
     */
    @Column(name = "informal_amount")
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        boolean hasFormal = amount != null && unit != null;
        boolean hasInformal = informal != null && !informal.isBlank();

        if ((amount == null) != (unit == null)) {
            throw new IllegalStateException("Formal requires both amount and unit.");
        }

        if (hasFormal == hasInformal) {
            throw new IllegalStateException("Must set either formal (amount+unit) OR informal, but not both.");
        }
    }

    /**
     * Checks if two recipeIngredients are equal in values.
     * @param o   the reference object with which to compare.
     * @return    true if objects are equal.
     */
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
     * Since informal and formal units can not happen at the same time
     * if an amount is set then the informal becomes null
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
     * Since informal and formal units can not happen at the same time
     * if an informal amount is set then the amount and unit becomes null
     */
    public void setInformal(String informal) {
        this.informal = informal;
        this.unit = null;
        this.amount = null;
    }

    /**
     * Displays the amount.
     * @return a String of the format "amount unit"
     */
    public String displayAmount() {
        if (informal != null && !informal.isBlank()) {
            return informal;
        } else if (amount != null && unit != null) {
            return amount + " " + unit;
        } else {
            return "";
        }
    }

    /**
     * Stringifies the recipeIngredient.
     * @return String of the format "name amount unit"
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (this.informal != null) {
            sb.append(this.informal);
        } else {
            sb.append(this.amount).append(" ").append(this.unit);
        }

        sb.append(" ").append(this.ingredient.getName());
        return sb.toString();
    }

    /**
     * Checks if recipeIngredient is formal.
     * @return true if recipeIngredient is formal
     *         false otherwise
     */
    public boolean isFormal() {
        return this.informal == null;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public RecipeIngredientKey getRecipeId() {
        return id;
    }

    public void setId(RecipeIngredientKey id) {
        this.id = id;
    }
}


