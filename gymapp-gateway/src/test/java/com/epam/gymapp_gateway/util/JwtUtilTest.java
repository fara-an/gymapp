package com.epam.gymapp_gateway.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "my-secret-key-123456789012345678901234567890";

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        var field = JwtUtil.class.getDeclaredField("secretValue");
        field.setAccessible(true);
        field.set(jwtUtil, SECRET);
        jwtUtil.init();
    }

    private String generateToken(long expirationMillisOffset, String secret) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillisOffset);

        return Jwts.builder()
                .setSubject("testUser")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(
                        io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)),
                        SignatureAlgorithm.HS256
                )
                .compact();
    }

    @Test
    void validateToken_withValidToken_shouldNotThrow() {
        String token = generateToken(60_000, SECRET);
        assertDoesNotThrow(() -> JwtUtil.validateToken(token));
    }

    @Test
    void validateToken_withExpiredToken_shouldThrowJwtException() {
        String token = generateToken(-60_000, SECRET); // expired 60 seconds ago
        JwtException ex = assertThrows(JwtException.class, () -> JwtUtil.validateToken(token));
        assertTrue(ex.getMessage().contains("JWT expired"));
    }

    @Test
    void validateToken_withInvalidSignature_shouldThrowJwtException() {
        String token = generateToken(60_000, "another-secret-key-12345678901234567890");
        JwtException ex = assertThrows(JwtException.class, () -> JwtUtil.validateToken(token));
        assertTrue(ex.getMessage().contains("JWT is invalid"));
    }
}
