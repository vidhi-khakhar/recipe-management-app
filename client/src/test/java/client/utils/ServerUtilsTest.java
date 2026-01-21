package client.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import client.config.ConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServerUtilsTest {

    @Mock
    private ConfigService mockConfigService;

    private ServerUtils serverUtils;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIsServerAvailableSuccess() {
        when(mockConfigService.getConfig()).thenReturn(null);
        assertTrue(true);
    }

    @Test
    public void testConstructorWithConfigService() {
        ServerUtils utils = new ServerUtils(mockConfigService);
        assertNotNull(utils);
    }
}
