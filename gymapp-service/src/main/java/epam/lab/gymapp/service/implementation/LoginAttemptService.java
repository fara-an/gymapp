package epam.lab.gymapp.service.implementation;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_TIME_MILLIS = 5 * 60 * 1000;

    private final Map<String, LoginFailRecord> attemptsCache = new ConcurrentHashMap<>();

    public void loginFailed(String username) {
        LoginFailRecord record = attemptsCache.getOrDefault(username, new LoginFailRecord());
        record.incrementAttempts();
        attemptsCache.put(username, record);
    }

    public void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public boolean isBlocked(String username) {
        LoginFailRecord record = attemptsCache.get(username);
        if (record == null) return false;
        if (record.getAttempts() < MAX_ATTEMPTS) return false;

        long elapsedTime = System.currentTimeMillis() - record.getLastFailedTime();
        if (elapsedTime > BLOCK_TIME_MILLIS) {
            attemptsCache.remove(username);
            return false;
        }
        return true;
    }

    private static class LoginFailRecord {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private long lastFailedTime = System.currentTimeMillis();

        void incrementAttempts() {
            attempts.incrementAndGet();
            lastFailedTime = System.currentTimeMillis();
        }

        int getAttempts() {
            return attempts.get();
        }

        long getLastFailedTime() {
            return lastFailedTime;
        }
    }
}
