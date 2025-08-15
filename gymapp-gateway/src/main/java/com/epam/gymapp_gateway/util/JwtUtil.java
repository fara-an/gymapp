package com.epam.gymapp_gateway.util;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretValue;

    private static String SECRET;

    @PostConstruct
    public void  init(){
        SECRET=secretValue;
    }


    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    public static void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException ex) {
            throw new JwtException("JWT expired", ex);
        } catch (JwtException ex) {
            throw new JwtException("JWT is invalid", ex);
        }
    }


}
