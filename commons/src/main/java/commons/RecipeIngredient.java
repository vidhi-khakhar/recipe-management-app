package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.*;

@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Many lines belong to one recipe
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    @JsonIgnore // prevents infinite JSON loops (Recipe -> RecipeIngredient -> Recipe -> ...)
    private Recipe recipe;

    // One ingredient can be used in many recipes
    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    /**
     * Formal amount (e.g. 100). For informal amounts, leave null.
     */
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
    private String informal;

    @SuppressWarnings("unused")
    protected RecipeIngredient() {}

    /**
     * Creates a recipe ingredient that is an ingredient in recipe
     * @param recipe
     * @param ingredient
     */
    public RecipeIngredient(Recipe recipe, Ingredient ingredient) {
        this.recipe = Objects.requireNonNull(recipe);
        this.ingredient = Objects.requireNonNull(ingredient);
    }

    // Getters/setters

    public Long getId() { return id; }

    public Recipe getRecipe() { return recipe; }

    public Ingredient getIngredient() { return ingredient; }

    public Double getAmount() { return amount; }

    public void setAmount(Double amount) { this.amount = amount; }

    public Unit getUnit() { return unit; }

    public void setUnit(Unit unit) { this.unit = unit; }

    public String getInformal() { return informal; }

    public void setInformal(String informal) { this.informal = informal; }
}

}
