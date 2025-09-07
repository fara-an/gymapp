package epam.lab.gymapp.exceptions;

public class EntityNotFoundException extends DaoException {

    public EntityNotFoundException(String msg) {
        super(msg);
    }

    @Override
    public String getUserMessage() {
        return "Requested entity  was not found";
    }
}
