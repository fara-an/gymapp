package epam.lab.gymapp.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class EndpointLogger {

    @Bean
    public ApplicationRunner logEndpoints(@Qualifier("requestMappingHandlerMapping")RequestMappingHandlerMapping mapping) {
        return args -> mapping.getHandlerMethods()
            .forEach((key, value) -> System.out.println("📌 " + key + " -> " + value));
    }
}
