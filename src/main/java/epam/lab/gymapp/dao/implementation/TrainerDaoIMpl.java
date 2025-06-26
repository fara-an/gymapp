package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
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
import java.util.Collections;
import java.util.List;
@Repository
public class TrainerDaoIMpl extends BaseDao<Trainer, Long> implements TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoIMpl.class);
    private static final String LOG_FETCH_UNASSIGNED_TRAINERS = "DAO: Fetching trainers not assigned to trainee '{}'";
    private static final String LOG_TRAINEE_NOT_FOUND = "DAO: No trainee found with username '{}'";
    private static final String LOG_UNASSIGNED_TRAINERS_FOUND_SUCCESS = "DAO: Retrieved Trainers {} not assigned to trainee '{}'";
    private static final String LOG_ERROR_FETCH_UNASSIGNED_TRAINERS = "DAO: Error fetching unassigned trainers for trainee '{}'";
    private static final String LOG_ERROR_FETCH_UNASSIGNED_TRAINERS_TEMPLATE = "DAO: Error fetching unassigned trainers for trainee '%s'";

    public TrainerDaoIMpl(SessionFactory sessionFactory) {
        super(Trainer.class, sessionFactory);
    }


    @Override
    public List<Training> getTrainerTrainings(
            String trainerUsername,
            LocalDateTime fromDate,
            LocalDateTime toDate,
            String traineeName) {

        LOGGER.debug("DAO: Fetching trainings for trainer='{}' from={} to={} trainee='{}' ",
                trainerUsername, fromDate, toDate, traineeName);

        try {
            Session session = getSessionFactory().getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);
            Root<Training> root = cq.from(Training.class);

            /* ---------- 1. Build predicates dynamically ---------- */
            List<Predicate> where = new ArrayList<>();

            // mandatory
            where.add(cb.equal(root.get("trainer").get("userName"), trainerUsername));

            // optional

            if (traineeName != null && !traineeName.isBlank()) {
                where.add(cb.equal(root.get("trainee").get("userName"), traineeName));
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
            String msg = String.format("DAO: Error retrieving trainings for trainer %s", trainerUsername);
            LOGGER.error(msg, ex);
            throw new DaoException(msg, ex);
        }
    }

    @Override
    public List<Trainer> trainersNotAssignedToTrainee(String traineeUsername) {
        LOGGER.debug(LOG_FETCH_UNASSIGNED_TRAINERS, traineeUsername);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String getTraineeHql = "from Trainee t where t.userName = :userName";
            Trainee trainee = session.createQuery(getTraineeHql, Trainee.class)
                    .setParameter("userName", traineeUsername)
                    .uniqueResult();
            if (trainee == null) {
                LOGGER.debug(LOG_TRAINEE_NOT_FOUND, traineeUsername);
                return Collections.emptyList();
            }
            String hql = """
                    select tr
                    from Trainer tr
                    where :trainee not in elements(tr.trainees)
                    """;

            List<Trainer> result = session.createQuery(hql, Trainer.class)
                    .setParameter("trainee", trainee)
                    .getResultList();

            LOGGER.debug(LOG_UNASSIGNED_TRAINERS_FOUND_SUCCESS, result.size(), traineeUsername);
            return result;
        } catch (Exception e) {
            LOGGER.error(LOG_ERROR_FETCH_UNASSIGNED_TRAINERS, traineeUsername);
            String errorMsg=String.format(LOG_ERROR_FETCH_UNASSIGNED_TRAINERS_TEMPLATE, traineeUsername);
            throw new DaoException(errorMsg, e);
        }

    }
}
