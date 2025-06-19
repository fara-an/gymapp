package epam.lab.gymapp.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import epam.lab.gymapp.dto.Credentials;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JWTService {

    private static final String USER_NAME="USERNAME";
    private static final String PASSWORD="PASSWORD";

    @Value("${jwt.algorithm.key}")
    private String algorithmKey;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;

    private Algorithm algorithm;

    @PostConstruct
    public void postConstruct(){
        algorithm=Algorithm.HMAC256(algorithmKey);
    }

    public String generateToken(Credentials credentials){
        return JWT.create()
                .withClaim(USER_NAME, credentials.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+(1000*expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);

    }

}
