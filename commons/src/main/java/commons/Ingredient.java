package commons;


import jakarta.persistence.*;
import jakarta.persistence.criteria.Order;

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
    @Id
    @GeneratedValue
    private Long id;

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
     * language of the ingredient, default "en"
     */
    @Column(nullable = false)
    private String language = "en";

    /**
     * Default constructor for JPA.
     */
    protected Ingredient() {

    }

    /**
     * Constructor with all fields except id and version.
     * @param name Name of the ingredient.
     * @param proteinPer100g Protein content per 100g.
     * @param fatPer100g Fat content per 100g.
     * @param carbsPer100g Carbohydrate content per 100g.
     * @param language Language code (e.g., "en", "nl", "de").
     */
    public Ingredient(String name, double proteinPer100g, double fatPer100g, double carbsPer100g, String language) {
        this.name = Objects.requireNonNull(name, "Name of an ingredient cannot be null");
        this.proteinPer100g = proteinPer100g;
        this.fatPer100g = fatPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = calculatingKcal(proteinPer100g, fatPer100g, carbsPer100g);

        if (language == null || language.trim().length() != 2) {
            this.language = "en";
        } else {
            this.language = language.trim().toLowerCase();
        }
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


    public Long getId() { return id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getproteinPer100g() {
        return proteinPer100g;
    }

    public void setproteinPer100g(double proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    public double getfatPer100g() {
        return fatPer100g;
    }

    public void setfatPer100g(double fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    public double getcarbsPer100g() {
        return carbsPer100g;
    }

    public void setcarbsPer100g(double carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
    }

    public double getkcalPer100g() {
        return kcalPer100g;
    }
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}