package server;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import commons.Ingredient;
import server.database.IngredientRepository;


/**
 * Making a Service that Spring will handle itself
 */
@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final EntityManager entityManager;


    /**
     * the autowired makes sure that Spring injects the right dependencies
     * @param ingredientRepository the ingredient repository
     * @param entityManager the entity manager
     */
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository,
                             EntityManager entityManager) {
        this.ingredientRepository = ingredientRepository;
        this.entityManager = entityManager;
    }

    /**
     * Update the name of an ingredient with optimistic locking
     * @param id the id of the ingredient to update
     * @param newName the new name for the ingredient
     */
    public void updateIngredientName(Long id, String newName) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .map(i -> {
                    i.setName(newName);
                    return i;
                })
                .orElse(null);

        if (ingredient != null) {
            try {
                ingredientRepository.save(ingredient);
            } catch (OptimisticLockException e) {
                entityManager.refresh(ingredient);
            }
        }
    }
}