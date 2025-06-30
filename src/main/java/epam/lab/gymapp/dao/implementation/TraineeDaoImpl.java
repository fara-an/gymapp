package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.exceptions.DaoException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TraineeDaoImpl extends BaseDao<Trainee, Long> implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDaoImpl.class);

    public TraineeDaoImpl(SessionFactory sessionFactory) {
        super(Trainee.class, sessionFactory);
    }

    @Override
    public List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String trainerName,
            String trainingType) {

        LOGGER.debug("DAO: Fetching trainings for trainee='{}' from={} to={} trainer='{}' type='{}'",
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        try {
            Session session = getSessionFactory().getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);
            Root<Training> root = cq.from(Training.class);

            /* ---------- 1. Build predicates dynamically ---------- */
            List<Predicate> where = new ArrayList<>();

            // mandatory
            where.add(cb.equal(root.get("trainee").get("userName"), traineeUsername));

            // optional
            if (trainingType != null && !trainingType.isBlank()) {
                where.add(cb.equal(root.get("trainingType").get("name"), trainingType));
            }
            if (trainerName != null && !trainerName.isBlank()) {
                where.add(cb.equal(root.get("trainer").get("userName"), trainerName));
            }
            if (fromDate != null) {
                // session must START no earlier than fromDate
                where.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), fromDate));
            }
            if (toDate != null) {
                // session must START no later than toDate
                // (If you really want «session must END before toDate» see note ↓)
                where.add(cb.lessThanOrEqualTo(root.get("trainingDate"), toDate));
            }

            /* ---------- 2. Execute ---------- */
            cq.select(root).where(where.toArray(new Predicate[0]));
            return session.createQuery(cq).getResultList();

        } catch (Exception ex) {
            String msg = String.format("DAO: Error retrieving trainings for trainee %s", traineeUsername);
            LOGGER.error(msg, ex);
            throw new DaoException(msg, ex);
        }
    }
}
