package server;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository implements JpaRepository<Recipe, Long> {
}
