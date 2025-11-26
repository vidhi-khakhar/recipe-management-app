package commons;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;


@Entity
@Table(name = "ingredient")
public class Ingredient {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ingredientID;


    @Column(nullable = false)
    private String name;

    @Column(name = "protein_per_100g")
    private int protein_per_100g;

    @Column(name = "fat_per_100g")
    private int fat_per_100g;

    @Column(name = "carbs_per_100g")
    private int carbs_per_100g;

    @Column(name = "kcal_per_100g")
    private int kcal_per_100g;

    @Column(nullable = false)
    private String language = "en";


    public Ingredient(String name, int protein_per_100g, int fat_per_100g, int carbs_per_100g, int kcal_per_100g, String language) {
        this.name = Objects.requireNonNull(name, "Name of an ingredient cannot be null");
        this.protein_per_100g = protein_per_100g;
        this.fat_per_100g = fat_per_100g;
        this.carbs_per_100g = carbs_per_100g;
        this.kcal_per_100g = calculatingKcal(protein_per_100g, fat_per_100g, carbs_per_100g);

        if (language == null || language.trim().length() != 2) {
            this.language = "en";
        } else {
            this.language = language.trim().toLowerCase();
        }
    }

    public static int calculatingKcal(int protein, int fat, int carbs) {
        int kcal_per_100g = 4 * (protein + carbs) + 9 * fat;
        return kcal_per_100g;
    }


    public Long getIngredientID() {
        return ingredientID;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProtein_per_100g() {
        return protein_per_100g;
    }

    public void setProtein_per_100g(int protein_per_100g) {
        this.protein_per_100g = protein_per_100g;
    }

    public int getFat_per_100g() {
        return fat_per_100g;
    }

    public void setFat_per_100g(int fat_per_100g) {
        this.fat_per_100g = fat_per_100g;
    }

    public int getCarbs_per_100g() {
        return carbs_per_100g;
    }

    public void setCarbs_per_100g(int carbs_per_100g) {
        this.carbs_per_100g = carbs_per_100g;
    }

    public int getKcal_per_100g() {
        return kcal_per_100g;
    }

    public void setKcal_per_100g(int kcal_per_100g) {
        this.kcal_per_100g = kcal_per_100g;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


}