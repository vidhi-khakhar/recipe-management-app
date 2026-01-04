package commons;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * Ingredient Entity. Database has columns: id (unmodifiable), name,
 * proteinPer100g, fatPer100g, carbsPer100g, kcalPer100g, language
 */
@Entity
@Table(name = "ingredient")
public class Ingredient {

    /**
     * id identifies the ingredient in the database. It is the primary key.
     */
    @Id // Specifies the primary key.
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-generated primary key.
    private Long ingredientId;

    /**
     * Version field for optimistic locking.
     */
    @Version
    private Long version;

    /**
     * name of the ingredient shouldn't be null.
     */
    @Column(nullable = false)
    private String name;

    /**
     * nutritional values per 100g of the ingredient
     */
    @Column(name = "proteinPer100g")
    private double proteinPer100g;

    /**
     * nutritional values per 100g of the ingredient
     */
    @Column(name = "fatPer100g")
    private double fatPer100g;

    /**
     * nutritional values per 100g of the ingredient
     */
    @Column(name = "carbsPer100g")
    private double carbsPer100g;

    /**
     * nutritional values per 100g of the ingredient
     */
    @Column(name = "kcalPer100g")
    private double kcalPer100g;

    /**
     * Default constructor for JPA.
     */
    protected Ingredient() {

    }

    /**
     * Constructor with all fields except id and version.
     *
     * @param name Name of the ingredient.
     * @param proteinPer100g Protein content per 100g.
     * @param fatPer100g Fat content per 100g.
     * @param carbsPer100g Carbohydrate content per 100g.
     */
    public Ingredient(String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        this.name = Objects.requireNonNull(name, "Name of an ingredient cannot be null");
        if (proteinPer100g < 0) {
            throw new IllegalArgumentException("Protein per 100g cannot be negative");
        }
        if (fatPer100g < 0) {
            throw new IllegalArgumentException("Fat per 100g cannot be negative");
        }
        if (carbsPer100g < 0) {
            throw new IllegalArgumentException("Carbs per 100g cannot be negative");
        }
        this.proteinPer100g = proteinPer100g;
        this.fatPer100g = fatPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = calculatingKcal(proteinPer100g, fatPer100g, carbsPer100g);

    }

    /**
     * Calculate kcal per 100g based on protein, fat, and carbs.
     *
     * @param protein Protein content per 100g.
     * @param fat Fat content per 100g.
     * @param carbs Carbohydrate content per 100g.
     * @return Calculated kcal per 100g.
     */
    public static double calculatingKcal(double protein, double fat, double carbs) {
        double kcalPer100g = 4 * (protein + carbs) + 9 * fat;
        return kcalPer100g;
    }

    /**
     * @param anotherIngredient another ingredient
     * @return whether two ingredients are equal Note: Reflection means the
     * library automatically looks at the object’s fields at runtime instead of
     * you manually listing them. Uses reflection to compare all fields of this
     * object with the other object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Ingredient)) {
            return false;
        }
        Ingredient other = (Ingredient) o;
        return ingredientId != null && ingredientId.equals(other.ingredientId);
    }

    @Override
    public int hashCode() {
        return ingredientId != null ? ingredientId.hashCode() : 0;
    }

    public Long getId() {
        return ingredientId;
    }

    public void setId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }
    
    /**
     * Sets the protein content per 100g.
     *
     * @param proteinPer100g Protein content per 100g.
     * @throws IllegalArgumentException if proteinPer100g is negative.
     */
    public void setProteinPer100g(double proteinPer100g) {
        if (proteinPer100g < 0) {
            throw new IllegalArgumentException("Protein per 100g cannot be negative");
        }
        this.proteinPer100g = proteinPer100g;
        this.kcalPer100g = calculatingKcal(proteinPer100g, this.fatPer100g, this.carbsPer100g);
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    /**
     * Sets the fat content per 100g.
     *
     * @param fatPer100g Fat content per 100g.
     * @throws IllegalArgumentException if fatPer100g is negative.
     */
    public void setFatPer100g(double fatPer100g) {
        if (fatPer100g < 0) {
            throw new IllegalArgumentException("Fat per 100g cannot be negative");
        }
        this.fatPer100g = fatPer100g;
        this.kcalPer100g = calculatingKcal(this.proteinPer100g, fatPer100g, this.carbsPer100g);
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    /**
     * Sets the carbohydrate content per 100g.
     *
     * @param carbsPer100g Carbohydrate content per 100g.
     * @throws IllegalArgumentException if carbsPer100g is negative.
     */
    public void setCarbsPer100g(double carbsPer100g) {
        if (carbsPer100g < 0) {
            throw new IllegalArgumentException("Carbs per 100g cannot be negative");
        }
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = calculatingKcal(this.proteinPer100g, this.fatPer100g, carbsPer100g);
    }

    public double getKcalPer100g() {
        return kcalPer100g;
    }

    public void setKcalPer100g(double kcalPer100g) {
        this.kcalPer100g = kcalPer100g;
    }

    @Override
    public String toString() {
        return name;
    }
}
