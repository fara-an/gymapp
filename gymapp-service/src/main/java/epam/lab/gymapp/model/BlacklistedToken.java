package epam.lab.gymapp.model;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@EqualsAndHashCode
@Getter
public class BlacklistedToken {

    private final String token;

    private final Instant expiryDate;

    public BlacklistedToken(String token, Instant expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }

}
