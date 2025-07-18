package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dao.implementation.TokenBlacklistDao;
import epam.lab.gymapp.model.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistDao blacklistDao;

    public void blacklistToken(String token, Instant expiry) {
        blacklistDao.save(new BlacklistedToken(token, expiry));
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistDao.isBlacklisted(token);
    }

    @Scheduled(cron = "0 0 * * * *") // Optional cleanup every hour
    public void cleanupExpiredTokens() {
        blacklistDao.deleteExpiredTokens();
    }
}
