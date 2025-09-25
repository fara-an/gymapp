package epam.lab.gymapp.cucumber;

import io.cucumber.spring.ScenarioScope;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
@Setter
@Getter
public class TestContextJwt {
    private String jwtToken;
}
