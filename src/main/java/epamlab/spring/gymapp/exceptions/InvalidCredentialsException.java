package epamlab.spring.gymapp.exceptions;

public class InvalidCredentialsException extends RuntimeException {
    private static final String INVALID_CREDENTIALS_ERROR_MSG = "Invalids credentials or user: %s";

    public InvalidCredentialsException(String username) {
        super(String.format(INVALID_CREDENTIALS_ERROR_MSG, username));
    }
}
