package epam.lab.gymapp.cucumber.integration;

import epam.lab.gymapp.configuration.TestHibernateConfig;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;


@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestHibernateConfig.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Sql(
        statements = {
                "TRUNCATE TABLE training RESTART IDENTITY CASCADE",
        },
        executionPhase = AFTER_TEST_METHOD
)
public class CucumberSpringITConfiguration {

    @ServiceConnection
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0").withReuse(false);

    public static GenericContainer<?> activeMq = new GenericContainer<>("apache/activemq-artemis:2.31.2")
            .withExposedPorts(61616, 8161)
            .withEnv("ARTEMIS_USER", "admin")
            .withEnv("ARTEMIS_PASSWORD", "admin")
            .withReuse(false);

    static {
        activeMq.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.activemq.broker-url",
                () -> "tcp://localhost:" + activeMq.getMappedPort(61616));
        registry.add("spring.activemq.user", () -> "admin");
        registry.add("spring.activemq.password", () -> "admin");
    }

}
