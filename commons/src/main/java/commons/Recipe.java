package commons;

import jakarta.persistence.*;
import java.util.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Recipe Entity.
 * Database has 6 columns: recipeId (unmodifiable), title, ingredients, instructions, language, serving
 * For now language is default "en", serving is default 1 since basic functionality doesnt require them
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
    @SuppressWarnings("unused")
    protected Recipe() { }

    /**
     * Constructor with only title, defaults language to "en".
     * @param title title of the recipe
     */
    public Recipe(String title) {
        this(title, "en");
    }

    /**
     * Recommended constructor for the Recipe.
     * @param title title of the recipe
     * We don't store recipeId because it is auto-generated.
     * We don't store the ingredients and instructions because the user can add them later.
     * language and serving are defaulted
     */
    public Recipe(String title, String language) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.ingredients = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.servings = 1;

        if (language == null || language.trim().length() != 2) {
        this.language = "en";
        } else {
            this.language = language.trim().toLowerCase();
        }
    }


    /**
     * Returns a deep copy of this recipe with a new title.
     * Ingredients and instructions lists are copied to avoid mutating the original recipe.
     * @param newTitle title for the cloned recipe
     * @return cloned Recipe object
     * A new recipe id is generated when the cloned recipe is saved to the database.
     */
    public Recipe cloneRecipe(String newTitle) {
        Recipe clone = new Recipe(newTitle, this.language);
        clone.setIngredients(new ArrayList<>(this.ingredients));
        clone.setInstructions(new ArrayList<>(this.instructions));
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
        if (language == null || language.trim().length() != 2) {
        this.language = "en";
        } else {
            this.language = language.trim().toLowerCase();
        }
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        if (servings < 1) {
            this.servings = 1;
        } else
        this.servings = servings;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    /**
     * @param anotherRecipe another recipe
     * @return whether two recipes are equal
     * Note: Reflection means the library automatically looks at the object’s fields at runtime instead of you manually listing them.
     * Uses reflection to compare all fields of this object with the other object.
     */

    @Override
    public boolean equals(Object anotherRecipe) {
        return EqualsBuilder.reflectionEquals(this,anotherRecipe);
    }

    /**
     * @return hashcode
     * Generates a hash code by reflecting over all fields of this object.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * @return String representation
     * Creates a multi-line string representation by inspecting all fields via reflection.
     * Example:
     * commons.Recipe@5f184fc6[
     *   recipeId=12
     *  title=Pesto Panini
     *  ingredients=[Bread, Pesto, Mozzarella]
     *  instructions=[Spread pesto, Add cheese, Grill sandwich]
     *  language=en
     *   servings=1
     *  ]
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}