package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface DeleteDao<T extends BaseEntity<ID>, ID> {

    Logger LOGGER = LoggerFactory.getLogger(DeleteDao.class);
    String LOG_DELETE_START = "{}: DAO DELETE - Initiating deletion for entity {}";
    String LOG_DELETE_SUCCESS = "{}: DAO DELETE - Entity deleted successfully with ID {}";
    String LOG_DELETE_ERROR = "{}: DAO DELETE - Failed to delete entity {} – {}";
    String ERROR_DELETE_TEMPLATE = "%s: DAO DELETE - Failed to delete entity %s";

    SessionFactory getSessionFactory();

    Class<T> getEntityClass();

    default void delete(ID id) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug(LOG_DELETE_START, className, id);

        try {
            Session session = getSessionFactory().getCurrentSession();
            T entity = session.byId(getEntityClass()).load(id);
            session.remove(entity);
            LOGGER.debug(LOG_DELETE_SUCCESS, className, id);
        }catch (Exception e ){
            LOGGER.error(LOG_DELETE_ERROR, className,id, e.getMessage());
            String errorMessage = String.format(ERROR_DELETE_TEMPLATE, className, id);
            throw  new DaoException(errorMessage, e);

        }

    }


}
