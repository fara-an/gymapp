package epam.lab.gymapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan(basePackages = "epam.lab.gymapp")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableWebMvc
public class ApplicationConfig {

}
