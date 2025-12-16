package server.database;

import commons.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
//
//    @Query("SELECT COUNT(r)" +
//            "FROM Recipe as r " +
//            "JOIN r.ingredients as ri " +
//            "WHERE ri.ingredient.id = :ingredientId")
//    long countByIngredientId(@Param("ingredientId")long ingredientId);
}
