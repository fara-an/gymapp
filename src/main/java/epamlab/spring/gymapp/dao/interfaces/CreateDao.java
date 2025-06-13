package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CreateDao<T extends BaseEntity<ID>, ID> {

    Logger LOGGER = LoggerFactory.getLogger(CreateDao.class);
    String LOG_CREATE_START = "{}: DAO CREATE - Initiating creation for entity {}";
    String LOG_CREATE_SUCCESS = "{}: DAO CREATE - Entity created successfully with ID {}";
    String LOG_CREATE_ERROR = "{}: DAO CREATE - Failed to create entity {} – {}";
    String ERROR_CREATE_TEMPLATE = "%s: DAO CREATE - Failed to create entity %s";


    SessionFactory getSessionFactory();

    Class<T> getEntityClass();

    default T create(T item) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug(LOG_CREATE_START, className, item);
        try {
            Session session = getSessionFactory().getCurrentSession();
            session.persist(item);
            session.flush();
            session.refresh(item);
            LOGGER.debug(LOG_CREATE_SUCCESS, className, item.getId());
            return item;
        } catch (Exception e) {
            LOGGER.error(LOG_CREATE_ERROR, className, item, e.getMessage());
            String errorMessage = String.format(ERROR_CREATE_TEMPLATE, className, item);
            throw new DaoException(errorMessage, e);

        }
    }

}
