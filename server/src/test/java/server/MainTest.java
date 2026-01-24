package server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Main.class)
class MainTest {

    @Test
    void contextLoads() {
        assertNotNull(Main.class);
    }

    @Test
    void mainClassExists() {
        assertNotNull(Main.class);
    }

    @Test
    void mainClassIsSpringBootApplication() {
        assertNotNull(Main.class.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }
}
