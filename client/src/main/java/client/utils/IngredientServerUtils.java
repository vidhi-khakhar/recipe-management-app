package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import client.config.ConfigService;
import com.google.inject.Inject;
import commons.Ingredient;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Utility class for communicating with the backend to manage ingredients.
 */

public class IngredientServerUtils {

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NOT_FOUND = 404;

    private final ConfigService configService;
    private final Client client;

    /**
     * Constructor injection via Guice
     */
    @Inject
    public IngredientServerUtils(ConfigService configService, Client client) {
        this.configService = configService;
        this.client = client;
    }

    /**
     * Convenience constructor for production use
     */
    public IngredientServerUtils(ConfigService configService) {
        this(configService, jakarta.ws.rs.client.ClientBuilder.newClient());
    }

    /** Convenience method */
    private String server() {
        return configService.getConfig().getServerUrl();
    }

    /**
     * Creates a new ingredient on the server.
     * @param ingredient the ingredient to create
     * @return the ID of the created ingredient
     * @throws ProcessingException if there's a network error or server error
     */
    public Long createIngredient(Ingredient ingredient)
        throws ProcessingException {
        try (
            Response response = client
                .target(server() + "/api/v1/ingredients")
                .request(APPLICATION_JSON)
                .post(Entity.json(ingredient))
        ) {
            if (
                response.getStatus() == HTTP_OK ||
                response.getStatus() == HTTP_CREATED
            ) {
                Long id = response.readEntity(Long.class); // ✅ read ONCE
                System.out.println("Ingredient created with ID: " + id);
                return id;
            } else {
                throw new ProcessingException(
                    "Failed to create ingredient, HTTP " + response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error creating ingredient: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Retrieves all ingredients from the server.
     * @return a list of all ingredients
     * @throws ProcessingException if there's a network error or server error
     */
    public List<Ingredient> getAllIngredients() throws ProcessingException {
        try (
            Response response = client
                .target(server() + "/api/v1/ingredients")
                .request(APPLICATION_JSON)
                .get()
        ) {
            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(
                    new jakarta.ws.rs.core.GenericType<List<Ingredient>>() {}
                );
            }
            throw new ProcessingException(
                "Failed to get ingredients, HTTP " + response.getStatus()
            );
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error getting ingredients: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Retrieves a specific ingredient by its ID.
     * @param id the ID of the ingredient to retrieve
     * @return the ingredient with the specified ID
     * @throws ProcessingException if there's a network error or server error
     * @throws NoSuchElementException if no ingredient with the specified ID exists
     */
    public Ingredient getIngredient(Long id)
        throws ProcessingException, NoSuchElementException {
        try (
            Response response = client
                .target(server() + "/api/v1/ingredients/" + id)
                .request(APPLICATION_JSON)
                .get()
        ) {
            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(Ingredient.class);
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Ingredient with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to get ingredient " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error getting ingredient " +
                    id +
                    ": " +
                    e.getMessage(),
                e
            );
        }
    }

    /**
     * Replaces an existing ingredient on the server with a new one.
     * @param id the ID of the ingredient to replace
     * @param ingredient the new ingredient data
     * @return true if the replacement was successful
     * @throws ProcessingException if there's a network error or server error during replacement
     * @throws NoSuchElementException if no ingredient with the specified ID exists
     */
    public boolean replaceIngredient(Long id, Ingredient ingredient)
        throws ProcessingException, NoSuchElementException {
        try (
            Response response = client
                .target(server() + "/api/v1/ingredients/" + id)
                .request(APPLICATION_JSON)
                .put(Entity.json(ingredient))
        ) {
            if (response.getStatus() == HTTP_OK) {
                return true;
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Ingredient with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to replace ingredient " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error replacing ingredient " +
                    id +
                    ": " +
                    e.getMessage(),
                e
            );
        }
    }

    /**
     * Deletes a ingredient from the server.
     * @param id the ID of the ingredient to delete
     * @return true if the deletion was successful
     * @throws ProcessingException if there's a network error or server error during deletion
     * @throws NoSuchElementException if no ingredient with the specified ID exists
     */
    public boolean deleteIngredient(Long id)
        throws ProcessingException, NoSuchElementException {
        try (
            Response response = client
                .target(server() + "/api/v1/ingredients/" + id)
                .request(APPLICATION_JSON)
                .delete()
        ) {
            if (response.getStatus() == HTTP_OK) {
                return true;
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Ingredient with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to delete ingredient " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error deleting ingredient " +
                    id +
                    ": " +
                    e.getMessage(),
                e
            );
        }
    }

    /**
     * Gets the usage count of an ingredient across all recipes.
     * @param ingredientId the ID of the ingredient
     * @return the number of recipes using the ingredient
     * @throws ProcessingException if there's a network error or server error during the request
     * @throws NoSuchElementException if no ingredient with the specified ID exists
     */
    public long getIngredientUsageCount(Long ingredientId)
        throws ProcessingException, NoSuchElementException {
        try (
            Response response = client
                .target(
                    server() + "/api/v1/ingredients/" + ingredientId + "/usage"
                )
                .request(APPLICATION_JSON)
                .get()
        ) {
            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(Long.class);
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Ingredient with id " + ingredientId + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to get ingredient usage, HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error getting ingredient usage: " + e.getMessage(),
                e
            );
        }
    }
}
