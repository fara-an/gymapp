package epam.lab.gymapp.config.resilience4j;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Resilience4jConfig {

    @Bean
    public CircuitBreaker trainerWorkloadCircuitBreaker(CircuitBreakerRegistry registry) {
      return   registry.circuitBreaker("trainerWorkloadCB");
    }
}
