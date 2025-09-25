package epam.lab.gymapp.cucumber.component;

import epam.lab.gymapp.configuration.TestHibernateConfig;
import epam.lab.gymapp.configuration.TrainerWorkloadClientServiceStubConfig;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestHibernateConfig.class, TrainerWorkloadClientServiceStubConfig.class})
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0").withReuse(false);

}
