package epam.lab.gymapp.exceptions;

public class UserInputException extends ApplicationException {

    public UserInputException(String message) {
        super(message);
    }

    public UserInputException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getUserMessage() {
        return "User entered invalid input";
    }
}
