package epam.lab.gymapp.health;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomDatabaseHealthIndicator implements HealthIndicator {

    private final SessionFactory sessionFactory;

    @Override
    public Health health() {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            Integer result = session.createNativeQuery("SELECT 1", Integer.class).getSingleResult();
            tx.commit();

            if (result != null && result == 1) {
                return Health.up().withDetail("database", "Database is up").build();
            } else {
                return Health.down().withDetail("database", "Database is down").build();
            }
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return Health.down(e).withDetail("database", "Not available").build();
        }
    }
}
