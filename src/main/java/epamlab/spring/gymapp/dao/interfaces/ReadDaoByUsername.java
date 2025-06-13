package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public interface ReadDaoByUsername<T extends BaseEntity<ID>, ID> {
    Logger LOGGER = LoggerFactory.getLogger(ReadDao.class);
    String LOG_READ_START = "{}: DAO READ - Initiating read for entity {}";
    String LOG_READ_ERROR = "{}: DAO READ - Failed to read entity {} – {}";
    String ERROR_READ_TEMPLATE = "%s: DAO READ - Failed to read entity %s";

    String USER_FIND_BY_USERNAME =
            "FROM %s e WHERE e.user.username = :username";


    SessionFactory getSessionFactory();

    Class<T> getEntityClass();

    default Optional<T> findByUsername(String username) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug(LOG_READ_START, className, username);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String query = String.format(USER_FIND_BY_USERNAME, className);
            Query<T> tQuery = session.createQuery(query, getEntityClass()).setParameter("username", username);
            return tQuery.uniqueResultOptional();


        } catch (Exception e) {
            LOGGER.error(LOG_READ_ERROR, className, username, e.getMessage());
            String errorMessage = String.format(ERROR_READ_TEMPLATE, className, username);
            throw new RuntimeException(errorMessage, e);
        }
    }

}
