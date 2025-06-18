package epamlab.spring.gymapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Credentials {
    private String username;
    private String password;

    public Credentials(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
        this.username = username;
        this.password = password;
    }
}
