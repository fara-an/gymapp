package epam.lab.gymapp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.DelegatingFilterProxy;


@SpringBootApplication (exclude = HibernateJpaAutoConfiguration.class)
@EnableTransactionManagement
@EnableJms
public class GymApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymApplication.class, args);

    }

    @Bean
    @Profile("!test")
    FilterRegistrationBean<DelegatingFilterProxy> authFilterRegistration() {
        FilterRegistrationBean<DelegatingFilterProxy> registration = new FilterRegistrationBean<>();
        registration.setFilter(new DelegatingFilterProxy("jwtAuthFilter"));
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    @Profile("!test")
    FilterRegistrationBean<DelegatingFilterProxy> transactionIdFilterRegistration() {
        FilterRegistrationBean<DelegatingFilterProxy> registration = new FilterRegistrationBean<>();
        registration.setFilter(new DelegatingFilterProxy("transactionIdFilter"));
        registration.addUrlPatterns("/*");
        registration.setOrder(2);
        return registration;
    }


}