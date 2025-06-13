package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface UpdateDao<T extends BaseEntity<ID>, ID> {
    Logger LOGGER = LoggerFactory.getLogger(UpdateDao.class);
    String LOG_UPDATE_START = "{}: DAO UPDATE - Initiating update for entity {}";
    String LOG_UPDATE_SUCCESS = "{}: DAO UPDATE - Entity updated successfully with ID {}";
    String LOG_UPDATE_ERROR = "{}: DAO UPDATE - Failed to update entity {} – {}";
    String ERROR_UPDATE_TEMPLATE = "%s: DAO UPDATE - Failed to update entity %s";

    SessionFactory getSessionFactory();

    Class<T> getEntityClass();

    default T update(T item) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug(LOG_UPDATE_START, className, item);

        try {
            Session session = getSessionFactory().getCurrentSession();
            session.merge(item);
            session.flush();
            session.refresh(item);

            LOGGER.debug(LOG_UPDATE_SUCCESS, className, item);
            return item;

        } catch (Exception e) {
            LOGGER.error(LOG_UPDATE_ERROR, className, item, e.getMessage());
            String errorMessage = String.format(ERROR_UPDATE_TEMPLATE, className, item);
            throw new DaoException(errorMessage, e);
        }
    }
}
