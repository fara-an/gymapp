package epam.lab.gymapp.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("my-super-secret-key-my-super-secret-key",Duration.ofMinutes(5));
        userDetails = new User("testUser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_ShouldContainCorrectUsername() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = new User("otherUser", "password", Collections.emptyList());
        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    void extractExpiration_ShouldReturnFutureInstant() {
        String token = jwtService.generateToken(userDetails);

        Instant expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.isAfter(Instant.now()));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_ForExpiredToken() throws InterruptedException {
        jwtService = new JwtService("my-super-secret-key-my-super-secret-key",Duration.ofMillis(1));
        String token = jwtService.generateToken(userDetails);

        Thread.sleep(5);


        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isTokenValid(token, userDetails));
    }
}
