package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.BaseEntity;
import org.hibernate.Session;

public  interface CreateReadUpdateDao <T extends BaseEntity<ID>,ID> extends CreateReadDao<T,ID>{
    default T update(T item) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug("{}: DAO UPDATE - Initiating update for entity {}", className, item);

        try {
            Session session = getSessionFactory().getCurrentSession();
            session.merge(item);
            session.flush();
            session.refresh(item);

            LOGGER.debug("{}: DAO UPDATE - Entity updated successfully with ID {}", className, item);
            return item;

        } catch (Exception e) {
            LOGGER.error("{}: DAO UPDATE - Failed to update entity {} – {}", className, item, e.getMessage());
            String errorMessage = String.format("%s: DAO UPDATE - Failed to update entity %s", className, item);
            throw new DaoException(errorMessage, e);
        }
    }
}
