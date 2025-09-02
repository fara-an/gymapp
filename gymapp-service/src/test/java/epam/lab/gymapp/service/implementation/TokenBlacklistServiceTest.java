package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.implementation.TokenBlacklistDao;
import epam.lab.gymapp.model.BlacklistedToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenBlacklistServiceTest {

    private TokenBlacklistDao blacklistDao;
    private TokenBlacklistService blacklistService;

    @BeforeEach
    void setUp() {
        blacklistDao = mock(TokenBlacklistDao.class);
        blacklistService = new TokenBlacklistService(blacklistDao);
    }

    @Test
    void blacklistToken_SavesTokenWithExpiry() {
        String token = "jwt-token";
        Instant expiry = Instant.now().plusSeconds(3600);

        blacklistService.blacklistToken(token, expiry);

        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistDao).save(captor.capture());

        BlacklistedToken savedToken = captor.getValue();
        assertThat(savedToken.getToken()).isEqualTo(token);
        assertThat(savedToken.getExpiryDate()).isEqualTo(expiry);
    }

    @Test
    void isTokenBlacklisted_ReturnsTrue_WhenDaoReturnsTrue() {
        String token = "jwt-token";
        when(blacklistDao.isBlacklisted(token)).thenReturn(true);

        boolean result = blacklistService.isTokenBlacklisted(token);

        assertThat(result).isTrue();
        verify(blacklistDao).isBlacklisted(token);
    }

    @Test
    void isTokenBlacklisted_ReturnsFalse_WhenDaoReturnsFalse() {
        String token = "jwt-token";
        when(blacklistDao.isBlacklisted(token)).thenReturn(false);

        boolean result = blacklistService.isTokenBlacklisted(token);

        assertThat(result).isFalse();
        verify(blacklistDao).isBlacklisted(token);
    }
}
