package epam.lab.gymapp.config.restTemplateConfig;

import epam.lab.gymapp.filter.perrequest.TransactionIdFilter;
import org.slf4j.MDC;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder.interceptors((request, body, execution) -> {
            String tx = MDC.get("transactionId");
            if (tx != null) {
                request.getHeaders().add(TransactionIdFilter.HEADER_NAME, tx);
            }
            return execution.execute(request, body);
        }).build();
    }

}
