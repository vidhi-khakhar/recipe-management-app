package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import client.config.ConfigService;
import commons.Recipe;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for RecipeServerUtils using Mockito
 */
public class RecipeServerUtilsTest {

    static {
        System.setProperty("net.bytebuddy.experimental", "true");
    }

    private Client mockClient;
    private WebTarget mockTarget;
    private Invocation.Builder mockBuilder;
    private Response mockResponse;
    private RecipeServerUtils utils;
    private static final String BASE_URL = "http://testserver/";

    @BeforeEach
    public void setUp() {
        mockTarget = mock(WebTarget.class);
        mockBuilder = mock(Invocation.Builder.class);
        mockResponse = mock(Response.class);
        mockClient = (Client) Proxy.newProxyInstance(
            Client.class.getClassLoader(),
            new Class[] { Client.class },
            (_, method, args) -> {
                if (
                    "target".equals(method.getName()) &&
                    args != null &&
                    args.length == 1 &&
                    args[0] instanceof String
                ) {
                    return mockTarget;
                }
                if ("close".equals(method.getName())) {
                    return null;
                }
                throw new UnsupportedOperationException(
                    "Method not supported in test proxy: " + method.getName()
                );
            }
        );
        utils = new RecipeServerUtils(mockClient, BASE_URL);
    }

    private void stubTarget(String path) {
        when(mockTarget.request(APPLICATION_JSON)).thenReturn(mockBuilder);
    }

    @Test
    public void testCreateRecipeSuccess() {
        Recipe recipe = new Recipe("Test Recipe");
        stubTarget("api/v1/recipes");
        when(mockBuilder.post(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(201);
        when(mockResponse.readEntity(Long.class)).thenReturn(1L);

        Long id = utils.createRecipe(recipe);
        assertEquals(1L, id);
    }

    @Test
    public void testCreateRecipeFailure() {
        Recipe recipe = new Recipe("Test Recipe");
        stubTarget("api/v1/recipes");
        when(mockBuilder.post(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(400);

        assertThrows(ProcessingException.class, () ->
            utils.createRecipe(recipe)
        );
    }

    @Test
    public void testGetAllRecipesSuccess() {
        stubTarget("api/v1/recipes");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        List<Recipe> list = new ArrayList<>();
        list.add(new Recipe("Test Recipe"));
        when(mockResponse.readEntity(any(GenericType.class))).thenReturn(list);

        List<Recipe> result = utils.getAllRecipes();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllRecipesFailure() {
        stubTarget("api/v1/recipes");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(500);

        assertThrows(ProcessingException.class, () -> utils.getAllRecipes());
    }

    @Test
    public void testGetRecipeSuccess() {
        Long id = 2L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        Recipe recipe = new Recipe("Test Recipe");
        when(mockResponse.readEntity(Recipe.class)).thenReturn(recipe);

        Recipe result = utils.getRecipe(id);
        assertNotNull(result);
    }

    @Test
    public void testGetRecipeNotFound() {
        Long id = 2L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () -> utils.getRecipe(id));
    }

    @Test
    public void testReplaceRecipeSuccess() {
        Long id = 3L;
        Recipe recipe = new Recipe("Test Recipe");
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.put(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        assertTrue(utils.replaceRecipe(id, recipe));
    }

    @Test
    public void testReplaceRecipeNotFound() {
        Long id = 3L;
        Recipe recipe = new Recipe("Test Recipe");
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.put(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.replaceRecipe(id, recipe)
        );
    }

    @Test
    public void testDeleteRecipeSuccess() {
        Long id = 4L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        assertTrue(utils.deleteRecipe(id));
    }

    @Test
    public void testDeleteRecipeNotFound() {
        Long id = 4L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.deleteRecipe(id)
        );
    }

    @Test
    public void testConstructorUrlNormalization() {
        RecipeServerUtils utilsNoSlash = new RecipeServerUtils(
            mockClient,
            "http://testserver"
        );
        stubTarget("api/v1/recipes");
        Recipe recipe = new Recipe("Test Recipe");
        when(mockBuilder.post(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(201);
        when(mockResponse.readEntity(Long.class)).thenReturn(1L);
        Long id = utilsNoSlash.createRecipe(recipe);
        assertEquals(1L, id);
    }


    @Test
    public void testGetRecipeUnexpectedStatus() {
        Long id = 5L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(403);
        assertThrows(ProcessingException.class, () -> utils.getRecipe(id));
    }

    @Test
    public void testReplaceRecipeUnexpectedStatus() {
        Long id = 3L;
        Recipe recipe = new Recipe("Test Recipe");
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.put(Entity.json(recipe))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(500);
        assertThrows(ProcessingException.class, () ->
            utils.replaceRecipe(id, recipe)
        );
    }

    @Test
    public void testDeleteRecipeUnexpectedStatus() {
        Long id = 4L;
        stubTarget("api/v1/recipes/" + id);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(500);
        assertThrows(ProcessingException.class, () -> utils.deleteRecipe(id));
    }
}
