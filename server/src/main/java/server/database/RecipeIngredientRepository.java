package server.database;

import commons.RecipeIngredient;
import commons.RecipeIngredientKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientKey> {
}
