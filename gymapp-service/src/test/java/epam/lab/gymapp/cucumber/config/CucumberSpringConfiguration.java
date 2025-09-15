package epam.lab.gymapp.cucumber.config;

import org.springframework.boot.test.context.SpringBootTest;
import io.cucumber.spring.CucumberContextConfiguration;
import epam.lab.gymapp.GymApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = GymApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
    // This class will be automatically detected by Cucumber
}
