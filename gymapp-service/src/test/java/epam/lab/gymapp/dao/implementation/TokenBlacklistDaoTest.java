package epam.lab.gymapp.dao.implementation;


import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.model.BlacklistedToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class TokenBlacklistDaoTest {

    @Mock
    private JwtService jwtService;

    private TokenBlacklistDao tokenBlacklistDao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenBlacklistDao = new TokenBlacklistDao(jwtService);
    }

    @Test
    void saveAndCheckBlacklistedToken() {
        String token = "dummy.jwt.token";
        Instant expiration = Instant.now().plusSeconds(3000);

        when(jwtService.extractExpiration(token)).thenReturn(expiration);

        BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiration);
        tokenBlacklistDao.save(blacklistedToken);

        assertTrue(tokenBlacklistDao.isBlacklisted(token));

        verify(jwtService, times(1)).extractExpiration(token);

    }

    @Test
    void shouldReturnFalseForNonBlacklistedToken(){
        String token = "Non blacklisted token";
        Instant expiration  = Instant.now().plusSeconds(1800);

        when(jwtService.extractExpiration(token)).thenReturn(expiration);

        assertFalse(tokenBlacklistDao.isBlacklisted(token));
        verify(jwtService).extractExpiration(token);
    }
}
