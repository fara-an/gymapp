package epam.lab.gymapp.configuration;

import epam.lab.gymapp.jwt.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@TestConfiguration
public class NoServiceConfig {

    @Bean
    public JwtService jwtService(){
        return new JwtService("nosecret", Duration.ZERO);
    }
}
