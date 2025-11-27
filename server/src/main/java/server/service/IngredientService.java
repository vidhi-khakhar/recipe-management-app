package server.service;

import commons.Ingredient;
import server.database.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final EntityManager entityManager;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, EntityManager entityManager) {
        this.ingredientRepository = ingredientRepository;
        this.entityManager = entityManager;
    }

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