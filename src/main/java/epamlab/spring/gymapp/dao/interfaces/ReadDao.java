package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

public interface ReadDao<T extends BaseEntity<ID>, ID> {

    Logger LOGGER = LoggerFactory.getLogger(ReadDao.class);
    String LOG_READ_START = "{}: DAO READ - Initiating read for entity {}";
    String LOG_READ_ERROR = "{}: DAO READ - Failed to read entity {} – {}";
    String ERROR_READ_TEMPLATE = "%s: DAO READ - Failed to read entity %s";

    SessionFactory getSessionFactory();

    Class<T> getEntityClass();

    default Optional<T> findByID(ID id) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug(LOG_READ_START, className, id);
        try {
            Session session = getSessionFactory().getCurrentSession();
            T result = session.get(getEntityClass(), id);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            LOGGER.error(LOG_READ_ERROR, className, id, e.getMessage());
            String errorMessage = String.format(ERROR_READ_TEMPLATE, className, id);
            throw new RuntimeException(errorMessage, e);

        }

    }
}
