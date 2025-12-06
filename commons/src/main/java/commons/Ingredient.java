package commons;


import jakarta.persistence.*;

import java.util.Objects;


/**
 * Ingredient Entity.
 * Database has columns: id (unmodifiable), name, proteinPer100g, fatPer100g, carbsPer100g, kcalPer100g, language
 */
@Entity
@Table(name = "ingredient")
public class Ingredient {


    /**
     * id identifies the ingredient in the database.
     * It is the primary key.
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
     * @param name           Name of the ingredient.
     * @param proteinPer100g Protein content per 100g.
     * @param fatPer100g     Fat content per 100g.
     * @param carbsPer100g   Carbohydrate content per 100g.
     */
    public Ingredient(String name, double proteinPer100g, double fatPer100g, double carbsPer100g) {
        this.name = Objects.requireNonNull(name, "Name of an ingredient cannot be null");
        this.proteinPer100g = proteinPer100g;
        this.fatPer100g = fatPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = calculatingKcal(proteinPer100g, fatPer100g, carbsPer100g);

    }

    /**
     * Calculate kcal per 100g based on protein, fat, and carbs.
     * @param protein Protein content per 100g.
     * @param fat Fat content per 100g.
     * @param carbs Carbohydrate content per 100g.
     * @return Calculated kcal per 100g.
     */
    public static double calculatingKcal(double protein, double fat, double carbs) {
        double kcalPer100g = 4 * (protein + carbs) + 9 * fat;
        return kcalPer100g;
    }


    public Long getId() {
            return ingredientId;
    }

    public void setId(Long id) {
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

    public void setProteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public void setFatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getCarbsPer100g() {
        return carbsPer100g;
    }

    public void setCarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
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