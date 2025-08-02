package epam.lab.gymapp.exceptions;

public class ServiceException extends RuntimeException {
    public ServiceException(String msg, Exception e) {
        super(msg,e);
    }
}
