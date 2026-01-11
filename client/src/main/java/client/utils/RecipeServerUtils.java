package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import com.google.inject.Inject;
import commons.Recipe;
import commons.RecipeIngredientKey;
import commons.RecipeIngredient;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import java.util.List;
import java.util.NoSuchElementException;
import org.glassfish.jersey.client.ClientConfig;
import client.config.ConfigService;

/**
 * Utility class for communicating with the backend to manage recipes.
 */
public class RecipeServerUtils {

    private static final int HTTP_OK = 200;
    private static final int HTTP_CREATED = 201;
    private static final int HTTP_NOT_FOUND = 404;

    private final ConfigService configService;
    private final Client client;

    private final String serverUrl;

    /**
     * Initializes the server utils with config and HTTP client.
     * Ensures the server URL always ends with a slash.
     * @param configService to be injected
     */
    @Inject
    public RecipeServerUtils(ConfigService configService) {
        this.configService = configService;
        this.client = ClientBuilder.newClient(new ClientConfig());
        this.serverUrl = configService.getConfig().getServerUrl().endsWith("/")
                ?  configService.getConfig().getServerUrl()
                : configService.getConfig().getServerUrl() + "/";
    }

    /**
     * Constructor allowing configuration of a custom Client and server URL.
     * @param client the JAX-RS client
     * @param serverUrl the base URL of the server
     */
    public RecipeServerUtils(Client client, String serverUrl, ConfigService configService) {
        this.configService = configService;
        this.client = client;
        this.serverUrl = serverUrl.endsWith("/") ? serverUrl : serverUrl + "/";
    }

    /**
     * Extra constructor for tests
     */
    public RecipeServerUtils(Client client, String serverUrl) {
        this.client = client;
        this.serverUrl = serverUrl.endsWith("/") ? serverUrl : serverUrl + "/";
        this.configService = null;
    }

    /**
     * Creates a new recipe on the server.
     * @param recipe the recipe to create
     * @return the ID of the created recipe
     * @throws ProcessingException if there's a network error or server error
     */
    public Long createRecipe(Recipe recipe) throws ProcessingException {
        try (
            Response response = client
                .target(serverUrl + "api/v1/recipes")
                .request(APPLICATION_JSON)
                .post(Entity.json(recipe))
        ) {
            if (response.getStatus() == HTTP_CREATED) {
                return response.readEntity(Long.class);
            } else {
                throw new ProcessingException(
                    "Failed to create recipe, HTTP " + response.getStatus()
                );
            }
        }
    }

    /**
     * Retrieves all recipes from the server.
     * @return a list of all recipes
     * @throws ProcessingException if there's a network error or server error
     */
    public List<Recipe> getAllRecipes() throws ProcessingException {
        try (
            Response response = client
                .target(serverUrl + "api/v1/recipes")
                .request(APPLICATION_JSON)
                .get()
        ) {
            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(new GenericType<List<Recipe>>() {});
            } else {
                throw new ProcessingException(
                    "Failed to get recipes, HTTP " + response.getStatus()
                );
            }
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
        try (
            Response response = client
                .target(serverUrl + "api/v1/recipes/" + id)
                .request(APPLICATION_JSON)
                .get()
        ) {
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
        try (
            Response response = client
                .target(serverUrl + "api/v1/recipes/" + id)
                .request(APPLICATION_JSON)
                .put(Entity.json(recipe))
        ) {
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
        try (
                Response response = client
                        .target(serverUrl + "api/v1/recipes/" + id)
                        .request(APPLICATION_JSON)
                        .delete()
        ) {
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
        }
    }

    // RECIPE INGREDIENT ENDPOINTS
    /**
     * Creates a new RecipeIngredient on the server.
     */
    public RecipeIngredientKey createRecipeIngredient(RecipeIngredient recipeIngredient) throws ProcessingException {
        try (Response response = client
                .target(serverUrl + "api/v1/recipes/ingredients")
                .request(APPLICATION_JSON)
                .post(Entity.json(recipeIngredient))) {

            if (response.getStatus() == HTTP_CREATED) {
                return response.readEntity(RecipeIngredientKey.class);
            }

            throw new ProcessingException(
                    "Failed to create recipe ingredient, HTTP "
                            + response.getStatus());
        }
    }

    /**
     * Gets all RecipeIngredients for a specific recipe.
     */
    public List<RecipeIngredient> getRecipeIngredients(Long recipeId)
            throws ProcessingException {

        try (Response response = client
                .target(serverUrl + "api/v1/recipes/" + recipeId + "/ingredients")
                .request(APPLICATION_JSON)
                .get()) {

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(
                        new GenericType<List<RecipeIngredient>>() {});
            }

            throw new ProcessingException(
                    "Failed to get recipe ingredients for recipe "
                            + recipeId + ", HTTP " + response.getStatus());
        }
    }

    /**
     * Gets all RecipeIngredients from the database.
     */
    public List<RecipeIngredient> getAllRecipeIngredients()
            throws ProcessingException {

        try (Response response = client
                .target(serverUrl + "api/v1/recipes/ingredients")
                .request(APPLICATION_JSON)
                .get()) {

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(
                        new GenericType<List<RecipeIngredient>>() {});
            }

            throw new ProcessingException(
                    "Failed to get all recipe ingredients, HTTP "
                            + response.getStatus());
        }
    }

    /**
     * Deletes a RecipeIngredient from a recipe.
     */
    public RecipeIngredientKey deleteRecipeIngredient(
            Long recipeId,
            Long ingredientId)
            throws ProcessingException, NoSuchElementException {

        try (Response response = client
                .target(serverUrl + "api/v1/recipes/ingredients/"
                        + recipeId + "/" + ingredientId)
                .request(APPLICATION_JSON)
                .delete()) {

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(RecipeIngredientKey.class);
            }

            if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                        "RecipeIngredient not found");
            }

            throw new ProcessingException(
                    "Failed to delete recipe ingredient, HTTP "
                            + response.getStatus());
        }
    }

    /**
     * Updates an existing RecipeIngredient.
     */
    public RecipeIngredientKey updateRecipeIngredient(
            Long recipeId,
            Long ingredientId,
            RecipeIngredient recipeIngredient)
            throws ProcessingException, NoSuchElementException {

        try (Response response = client
                .target(serverUrl + "api/v1/recipes/ingredients/"
                        + recipeId + "/" + ingredientId)
                .request(APPLICATION_JSON)
                .put(Entity.json(recipeIngredient))) {

            if (response.getStatus() == HTTP_OK) {
                return response.readEntity(RecipeIngredientKey.class);
            }

            if (response.getStatus() == HTTP_NOT_FOUND) {
                throw new NoSuchElementException(
                        "RecipeIngredient not found");
            }

            throw new ProcessingException(
                    "Failed to update recipe ingredient, HTTP "
                            + response.getStatus());
        }
    }
}
