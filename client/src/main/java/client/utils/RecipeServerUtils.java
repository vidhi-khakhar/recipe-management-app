package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import commons.Recipe;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.NoSuchElementException;
import org.glassfish.jersey.client.ClientConfig;

/**
 * Utility class for communicating with the backend to manage recipes.
 */
public class RecipeServerUtils {

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NOT_FOUND = 404;

    private static final String SERVER = "http://localhost:8080/";

    private static final Client CLIENT = ClientBuilder.newClient(
        new ClientConfig()
    );

    /**
     * Creates a new recipe on the server.
     * @param recipe the recipe to create
     * @return the ID of the created recipe
     * @throws ProcessingException if there's a network error or server error
     */
    public Long createRecipe(Recipe recipe) throws ProcessingException {
        try {
            Response response = CLIENT.target(SERVER + "api/v1/recipes")
                .request(APPLICATION_JSON)
                .post(Entity.json(recipe));

            if (response.getStatus() == HTTP_CREATED) {
                return response.readEntity(Long.class);
            } else {
                throw new ProcessingException(
                    "Failed to create recipe, HTTP " + response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException("Network error creating recipe");
        }
    }

    /**
     * Retrieves all recipes from the server.
     * @return a list of all recipes
     * @throws ProcessingException if there's a network error or server error
     */
    public List<Recipe> getAllRecipes() throws ProcessingException {
        try {
            Response response = CLIENT.target(SERVER + "api/v1/recipes")
                .request(APPLICATION_JSON)
                .get();

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(new GenericType<List<Recipe>>() {});
            } else {
                throw new ProcessingException(
                    "Failed to get recipes, HTTP " + response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException("Network error getting recipes");
        }
    }

    /**
     * Retrieves a specific recipe by its ID.
     * @param id the ID of the recipe to retrieve
     * @return the recipe with the specified ID
     * @throws ProcessingException if there's a network error or server error
     * @throws NoSuchElementException if no recipe with the specified ID exists
     */
    public Recipe getRecipe(Long id)
        throws ProcessingException, NoSuchElementException {
        try {
            Response response = CLIENT.target(SERVER + "api/v1/recipes/" + id)
                .request(APPLICATION_JSON)
                .get();

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(Recipe.class);
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Recipe with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to get recipe " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException("Network error getting recipe " + id);
        }
    }

    /**
     * Replaces an existing recipe on the server with a new one.
     * @param id the ID of the recipe to replace
     * @param recipe the new recipe data
     * @return true if the replacement was successful
     * @throws ProcessingException if there's a network error or server error during replacement
     * @throws NoSuchElementException if no recipe with the specified ID exists
     */
    public boolean replaceRecipe(Long id, Recipe recipe)
        throws ProcessingException, NoSuchElementException {
        try {
            Response response = CLIENT.target(SERVER + "api/v1/recipes/" + id)
                .request(APPLICATION_JSON)
                .put(Entity.json(recipe));

            if (response.getStatus() == HTTP_OK) {
                return true;
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Recipe with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to replace recipe " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error replacing recipe " + id
            );
        }
    }

    /**
     * Deletes a recipe from the server.
     * @param id the ID of the recipe to delete
     * @return true if the deletion was successful
     * @throws ProcessingException if there's a network error or server error during deletion
     * @throws NoSuchElementException if no recipe with the specified ID exists
     */
    public boolean deleteRecipe(Long id)
        throws ProcessingException, NoSuchElementException {
        try {
            Response response = CLIENT.target(SERVER + "api/v1/recipes/" + id)
                .request(APPLICATION_JSON)
                .delete();

            if (response.getStatus() == HTTP_OK) {
                return true;
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                    "Recipe with id " + id + " not found"
                );
            } else {
                throw new ProcessingException(
                    "Failed to delete recipe " +
                        id +
                        ", HTTP " +
                        response.getStatus()
                );
            }
        } catch (ProcessingException e) {
            throw new ProcessingException(
                "Network error deleting recipe " + id
            );
        }
    }
}
