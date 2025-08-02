package epam.lab.gymapp.dao.interfaces;

import epam.lab.gymapp.model.BaseEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public interface TrainingTypeDao <T extends BaseEntity<ID>,ID> {

    Logger LOGGER = LoggerFactory.getLogger(TrainingTypeDao.class);

    String FIND_BY_NAME= "FROM TrainingType t WHERE t.name = :name";

    SessionFactory getSessionFactory();

    Class<T> getEntityClass();


    default Optional<T> findByName(String name) {
        String className = getEntityClass().getSimpleName();
        LOGGER.debug( "{}: DAO READ - Initiating read name for entity {}", className, name);
        try {

            Session session = getSessionFactory().getCurrentSession();
            Query<T> tQuery = session.createQuery(FIND_BY_NAME, getEntityClass()).setParameter("name", name);
            return tQuery.uniqueResultOptional();

        } catch (Exception e) {
            LOGGER.error("{}: DAO READ - Failed to read name for entity  {} – {}", className, name, e.getMessage());
            String errorMessage = String.format("%s: DAO READ - Failed to read entity %s", className, name);
            throw new RuntimeException(errorMessage, e);
        }
    }


}
