package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.model.BlacklistedToken;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.*;


@Component
@RequiredArgsConstructor
public class TokenBlacklistDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenBlacklistDao.class);
    private final JwtService jwtService;
    private final Set<BlacklistedToken> blacklistedTokens = new HashSet<>();


    public void save(BlacklistedToken token) {
        LOGGER.debug("Token is being blacklisted ");
        blacklistedTokens.add(token);

    }

    public boolean isBlacklisted(String token) {
        LOGGER.debug("Checking whether token is blacklisted or not  ");
        Instant instant = jwtService.extractExpiration(token);
        BlacklistedToken jwtToken = new BlacklistedToken(token,instant);
        return blacklistedTokens.contains(jwtToken);
    }

}
