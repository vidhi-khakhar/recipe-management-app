package server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository implements JpaRepository<Recipe, Long> {
    // Implementation created automatically by Spring
}
