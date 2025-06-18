package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.model.BaseEntity;
import org.hibernate.Session;

public interface CrudDao<T extends BaseEntity<ID>, ID> extends CreateReadUpdateDao<T,ID> {
    default void delete(ID id) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug("{}: DAO DELETE - Initiating deletion for entity {}", className, id);

        try {
            Session session = getSessionFactory().getCurrentSession();
            T entity = session.byId(getEntityClass()).load(id);
            session.remove(entity);
            LOGGER.debug("{}: DAO UPDATE - Entity updated successfully with ID {}", className, id);
        }catch (Exception e ){
            LOGGER.error(" {}: DAO UPDATE - Failed to update entity {} – {}", className,id, e.getMessage());
            String errorMessage = String.format("%s: DAO DELETE - Failed to delete entity %s", className, id);
            throw  new DaoException(errorMessage, e);

        }

    }
}
