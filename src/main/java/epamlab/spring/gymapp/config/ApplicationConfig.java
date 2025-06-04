package epamlab.spring.gymapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "epamlab.spring.gymapp")
@PropertySource("classpath:storage.properties")
public class ApplicationConfig {


    @Bean
    StoragePostProcessor storagePostProcessor(Environment environment) {
        return new StoragePostProcessor(environment);
    }

}
