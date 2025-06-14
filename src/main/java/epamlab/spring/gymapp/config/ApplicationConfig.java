package epamlab.spring.gymapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "epamlab.spring.gymapp")
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
public class ApplicationConfig {

}
