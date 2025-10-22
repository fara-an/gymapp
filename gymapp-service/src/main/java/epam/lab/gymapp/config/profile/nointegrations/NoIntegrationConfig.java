package epam.lab.gymapp.config.profile.nointegrations;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("no-integrations")
public class NoIntegrationConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return new DummySessionFactory();
    }


    @Bean
    public org.springframework.jms.core.JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        return new org.springframework.jms.core.JmsTemplate(connectionFactory) {
            @Override
            public void convertAndSend(String destinationName, Object message) {
                System.out.println("🧩 JMS disabled — not sending message: " + message);
            }
        };
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        return new ConnectionFactory() {
            @Override
            public Connection createConnection() throws JMSException {
                return null;
            }

            @Override
            public Connection createConnection(String s, String s1) throws JMSException {
                return null;
            }

            @Override
            public JMSContext createContext() {
                return null;
            }

            @Override
            public JMSContext createContext(String s, String s1) {
                return null;
            }

            @Override
            public JMSContext createContext(String s, String s1, int i) {
                return null;
            }

            @Override
            public JMSContext createContext(int i) {
                return null;
            }
        };
    }
}
