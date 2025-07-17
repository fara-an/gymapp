package epam.lab.gymapp.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@RequiredArgsConstructor
public class ThreadPoolHealthIndicator implements HealthIndicator {

    private final ThreadPoolTaskExecutor taskExecutor;

    private static final int MAX_ALLOWED_QUEUE_SIZE = 100;
    private static final double MAX_UTILIZATION_THRESHOLD = 0.9;

    @Override
    public Health health() {
        ThreadPoolExecutor executor = taskExecutor.getThreadPoolExecutor();

        int activeCount = executor.getActiveCount();
        int poolSize = executor.getPoolSize();
        int maxPoolSize = executor.getMaximumPoolSize();
        int queueSize = executor.getQueue().size();

        double utilization = (double) activeCount / maxPoolSize;

        Health.Builder builder = utilization < MAX_UTILIZATION_THRESHOLD && queueSize < MAX_ALLOWED_QUEUE_SIZE
                ? Health.up()
                : Health.down();

        return builder.withDetail("activeThreads", activeCount)
                      .withDetail("poolSize", poolSize)
                      .withDetail("maxPoolSize", maxPoolSize)
                      .withDetail("queueSize", queueSize)
                      .withDetail("utilization", String.format("%.2f%%", utilization * 100))
                      .build();
    }
}
