package commons;

import jakarta.persistence.*;
import java.util.*;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Recipe Entity.
 * Database has 7 columns: recipeId(unmodifiable), title, ingredients, instructions, language, serving
 * For now language is default "EN", serving is default 1 since basic functionality doesnt require them
 */

@Entity // Marks this class as a JPA entity.
@Table(name = "recipe")
public class Recipe {

    /**
     * recipeID identifies the recipe in the database.
     * It is the primary key.
     */
    @Id // Specifies the primary key.
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-generated primary key.
    private Long recipeId;

    /**
     * title of the recipe shouldn't be a null.
     */
    @Column(nullable = false) // Makes the column non-nullable in the database.
    private String title;

    /**
     * Currently for basic functionality, ingredients are just stored as strings...
     * Later (for nutritional values), this will change to a structured IngredientAmount entity.
     */
    @ElementCollection
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient_text")
    private List<String> ingredients = new ArrayList<>();

    /**
     * The element collection tag makes it so that instructions are also stored in a separate table.
     * recipeID is used as a foreign key to link instructions to recipes so they are not orphaned 
     * (when recipe is deleted, data is deleted)
     * 
     *              recipe_instructions
            ---------------------------------------
            recipe_id | step_order | instruction
            ---------------------------------------
     */
    @ElementCollection
    @CollectionTable(
        name = "recipe_instructions",          // table name
        joinColumns = @JoinColumn(name = "recipe_id")  // foreign key to recipe
    )
    @Column(name = "instruction_text")         // column holding the instruction
    @OrderColumn(name = "instruction_order")  // column holding the order of instructions
    private List<String> instructions = new ArrayList<>();

    /**
     * Language code (e.g., "en", "nl", "de").
     * Required for live language filtering.
     */
    @Column(nullable = false)
    private String language = "en";  // default value


    /**
     * Number of servings.
     * Required for scaling.
     */
    @Column(nullable = false)
    private int servings = 1;


    /**
     * Default constructor is requested by JPA.
     */
    public Recipe() { }

    /**
     * Recommended constructor for the Recipe.
     * @param title title of the recipe
     * We don't store recipeId because it is auto-generated.
     * We don't store the ingredients and instructions because the user can add them later.
     * language and serving are defaulted
     */
    public Recipe(String title) {
        this.title = title;
    }


    /**
     * Returns a deep copy of this recipe with a new title.
     * Ingredients and instructions lists are copied to avoid mutating the original recipe.
     * @param newTitle title for the cloned recipe
     * @return cloned Recipe object
     */
    public Recipe cloneRecipe(String newTitle) {
    Recipe clone = new Recipe(newTitle);
    clone.setIngredients(new ArrayList<>(this.ingredients));
    clone.setInstructions(new ArrayList<>(this.instructions));
    clone.setLanguage(this.language);
    clone.setServings(this.servings);
    return clone;
}


   
    public Long getRecipeId() {
        return recipeId; //READ-ONLY
    }

    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }

    
    public List<String> getIngredients() {
        return ingredients;
    }

    
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    
    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

     public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    /**
     * @param anotherRecipe another recipe
     * @return whether two recipes are equal
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe other = (Recipe) o;
        return recipeId != null && recipeId.equals(other.recipeId);
    }


    /**
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(recipeId);
    }

    /**
     * @return String representation
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}