package epam.lab.gymapp.exceptions;

public class InvalidCredentialsException extends DaoException {
    private static final String INVALID_CREDENTIALS_ERROR_MSG = "Invalids credentials : %s";

    public InvalidCredentialsException(String username) {
        super(String.format(INVALID_CREDENTIALS_ERROR_MSG, username));
    }
}
