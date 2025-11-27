package server.database;

import commons.Ingredient;                  // entity from commons module
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Repository interface for Ingredient entities.
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}