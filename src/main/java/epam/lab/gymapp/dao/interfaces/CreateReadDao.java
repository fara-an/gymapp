package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public interface CreateReadDao <T extends BaseEntity<ID>,ID> {


    SessionFactory getSessionFactory();
    Class<T> getEntityClass();

    Logger LOGGER = LoggerFactory.getLogger(CreateReadDao.class);
    String
            FIND_BY_USERNAME= "FROM %s e WHERE e.userName = :userName";


    default Optional<T> findByID(ID id) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug("{}: DAO READ - Initiating read id for entity {}", className, id);
        try {
            Session session = getSessionFactory().getCurrentSession();
            T result = session.get(getEntityClass(), id);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            LOGGER.error("{}: DAO READ - Failed to read id entity {} – {}", className, id, e.getMessage());
            String errorMessage = String.format("%s: DAO READ - Failed to read entity %s", className, id);
            throw new RuntimeException(errorMessage, e);

        }
    }

    default Optional<T> findByUsername(String userName) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug( "{}: DAO READ - Initiating read username for entity {}", className, userName);
        try {

            Session session = getSessionFactory().getCurrentSession();
            String query = String.format(FIND_BY_USERNAME, className);
            Query<T> tQuery = session.createQuery(query, getEntityClass()).setParameter("userName", userName);
            return tQuery.uniqueResultOptional();

        } catch (Exception e) {
            LOGGER.error("{}: DAO READ - Failed to read username for entity  {} – {}", className, userName, e.getMessage());
            String errorMessage = String.format("%s: DAO READ - Failed to read entity %s", className, userName);
            throw new RuntimeException(errorMessage, e);
        }
    }


    default T create(T item) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug("{}: DAO CREATE - Initiating creation for entity {}", className, item);
        try {
            Session session = getSessionFactory().getCurrentSession();
            session.persist(item);
            session.flush();
            session.refresh(item);
            LOGGER.debug("{}: DAO CREATE - Successfully created entity {}", className, item.getId());
            return item;
        } catch (Exception e) {
            LOGGER.error("{}: DAO CREATE - Failed to create entity {} – {}", className, item, e.getMessage());
            String errorMessage = String.format("%s: DAO CREATE - Failed to create entity %s", className, item);
            throw new DaoException(errorMessage, e);

        }
    }





}
