package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.implementation.TokenBlacklistDao;
import epam.lab.gymapp.model.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistDao blacklistDao;

    public void blacklistToken(String token, Instant expiry) {
        blacklistDao.save(new BlacklistedToken(token, expiry));
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistDao.isBlacklisted(token);
    }
}
