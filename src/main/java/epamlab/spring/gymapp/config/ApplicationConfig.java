package epamlab.spring.gymapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "epamlab.spring.gymapp")
@PropertySource("classpath:application.properties")
public class ApplicationConfig {

}
