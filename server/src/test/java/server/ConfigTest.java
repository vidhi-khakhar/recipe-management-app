package server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    private final Config config = new Config();

    @Test
    void getRandomReturnsRandomInstance() {
        Random random = config.getRandom();
        assertNotNull(random);
    }

    @Test
    void getRandomReturnsNonNull() {
        Random random = config.getRandom();
        assertNotNull(random);
    }

    @Test
    void getRandomReturnsNewRandomInstance() {
        Random random = config.getRandom();
        assertNotNull(random);
        assertTrue(random instanceof Random);
    }
}
