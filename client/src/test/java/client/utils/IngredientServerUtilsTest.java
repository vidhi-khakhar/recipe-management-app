package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import client.config.Config;
import client.config.ConfigService;
import commons.Ingredient;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class IngredientServerUtilsTest {

    private Client mockClient;
    private WebTarget mockTarget;
    private Invocation.Builder mockBuilder;
    private Response mockResponse;
    private IngredientServerUtils utils;
    private ConfigService mockConfigService;
    private Config mockConfig;

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
        mockConfigService = mock(ConfigService.class);
        mockConfig = mock(Config.class);
        when(mockConfigService.getConfig()).thenReturn(mockConfig);
        when(mockConfig.getServerUrl()).thenReturn("http://testserver/");
        utils = new IngredientServerUtils(mockConfigService, mockClient);
    }

    private void stubTarget(String path) {
        when(mockTarget.request(APPLICATION_JSON)).thenReturn(mockBuilder);
    }

    @Test
    public void testCreateIngredientSuccess() {
        Ingredient ingredient = new Ingredient("Sugar", 0, 0, 100);
        stubTarget("api/v1/ingredients");
        when(mockBuilder.post(Entity.json(ingredient))).thenReturn(
            mockResponse
        );
        when(mockResponse.getStatus()).thenReturn(201);
        when(mockResponse.readEntity(Long.class)).thenReturn(1L);

        Long id = utils.createIngredient(ingredient);
        assertEquals(1L, id);
    }

    @Test
    public void testCreateIngredientFailure() {
        Ingredient ingredient = new Ingredient("Salt", 0, 0, 100);
        stubTarget("api/v1/ingredients");
        when(mockBuilder.post(Entity.json(ingredient))).thenReturn(
            mockResponse
        );
        when(mockResponse.getStatus()).thenReturn(400);

        assertThrows(ProcessingException.class, () ->
            utils.createIngredient(ingredient)
        );
    }

    @Test
    public void testGetAllIngredientsSuccess() {
        stubTarget("api/v1/ingredients");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        java.util.List<Ingredient> list = new java.util.ArrayList<>();
        list.add(new Ingredient("Sugar", 0, 0, 100));
        when(
            mockResponse.readEntity(new GenericType<List<Ingredient>>() {})
        ).thenReturn(list);

        List<Ingredient> result = utils.getAllIngredients();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetAllIngredientsFailure() {
        stubTarget("api/v1/ingredients");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(500);

        assertThrows(ProcessingException.class, () ->
            utils.getAllIngredients()
        );
    }

    @Test
    public void testGetIngredientSuccess() {
        Long id = 1L;
        Ingredient ingredient = new Ingredient("Sugar", 0, 0, 100);
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Ingredient.class)).thenReturn(ingredient);

        Ingredient result = utils.getIngredient(id);
        assertNotNull(result);
    }

    @Test
    public void testGetIngredientNotFound() {
        Long id = 99L;
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.getIngredient(id)
        );
    }

    @Test
    public void testReplaceIngredientSuccess() {
        Long id = 1L;
        Ingredient ingredient = new Ingredient("Salt", 0, 0, 100);
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.put(Entity.json(ingredient))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        assertTrue(utils.replaceIngredient(id, ingredient));
    }

    @Test
    public void testReplaceIngredientNotFound() {
        Long id = 99L;
        Ingredient ingredient = new Ingredient("Sugar", 0, 0, 100);
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.put(Entity.json(ingredient))).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.replaceIngredient(id, ingredient)
        );
    }

    @Test
    public void testDeleteIngredientSuccess() {
        Long id = 1L;
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        assertTrue(utils.deleteIngredient(id));
    }

    @Test
    public void testDeleteIngredientNotFound() {
        Long id = 99L;
        stubTarget("api/v1/ingredients/" + id);
        when(mockBuilder.delete()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.deleteIngredient(id)
        );
    }

    @Test
    public void testGetIngredientUsageCountSuccess() {
        Long id = 1L;
        stubTarget("api/v1/ingredients/" + id + "/usage");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.readEntity(Long.class)).thenReturn(5L);

        long count = utils.getIngredientUsageCount(id);
        assertEquals(5L, count);
    }

    @Test
    public void testGetIngredientUsageCountNotFound() {
        Long id = 99L;
        stubTarget("api/v1/ingredients/" + id + "/usage");
        when(mockBuilder.get()).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(404);

        assertThrows(NoSuchElementException.class, () ->
            utils.getIngredientUsageCount(id)
        );
    }

    @Test
    public void testConstructorWithConfigService() {
        IngredientServerUtils utils = new IngredientServerUtils(
            mockConfigService
        );
        assertNotNull(utils);
    }
}
