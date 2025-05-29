package springgymapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackages = "springgymapp")
@PropertySource("classpath:storage.properties")
public class ApplicationConfig  {


    @Bean
    StoragePostProcessor storagePostProcessor(Environment environment){
       return new StoragePostProcessor(environment);
    }

}
