package epam.lab.gymapp.exceptions;

public class DaoException extends ApplicationException{

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
