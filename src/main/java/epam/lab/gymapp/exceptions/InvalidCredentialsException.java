package epam.lab.gymapp.exceptions;

public class InvalidCredentialsException extends ApplicationException {

    private static final String INVALID_CREDENTIALS_ERROR_MSG = "Invalid credentials : %s";

    public InvalidCredentialsException(String username) {
        super(String.format(INVALID_CREDENTIALS_ERROR_MSG, username));
    }
}
