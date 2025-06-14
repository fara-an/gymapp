package epamlab.spring.gymapp.dto;

public record Credentials(String username, String password) {
    public Credentials {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password must not be null");
        }
    }
}
