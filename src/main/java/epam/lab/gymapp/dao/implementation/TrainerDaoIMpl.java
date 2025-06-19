package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
import epam.lab.gymapp.exceptions.DaoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
@Repository
public class TrainerDaoIMpl extends BaseDao<Trainer, Long> implements TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoIMpl.class);

    public TrainerDaoIMpl(SessionFactory sessionFactory) {
        super(Trainer.class, sessionFactory);
    }


    @Override
    public List<Training> getTrainerTrainings(Credentials credentials, String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername) {
        LOGGER.debug("DAO: Fetching trainings for trainer '{}', trainee '{}', from {} to {}", trainerUsername, traineeUsername, fromDate, toDate);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String hql = """
                    select t
                    from Training t
                    where t.trainer.userName = :trainerUsername
                      and t.trainee.userName = :traineeUsername
                      and t.trainingDate between :fromDate and :toDate
                    """;

            return session.createQuery(hql, Training.class)
                    .setParameter("trainerUsername", trainerUsername)
                    .setParameter("traineeUsername", traineeUsername)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.debug("DAO: Error retrieving trainings for trainer '{}', trainee '{}'", trainerUsername, traineeUsername, e);
            String errorMessage = String.format("DAO: Error retrieving trainings for trainer '%s', trainee '%s'", trainerUsername, traineeUsername);
            throw new DaoException(errorMessage, e);
        }
    }

    @Override
    public List<Trainer> trainersNotAssignedToTrainee(String traineeUsername) {
        LOGGER.debug("DAO: Fetching trainers not assigned to trainee '{}'", traineeUsername);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String getTraineeHql = "from Trainee t where t.userName = :userName";
            Trainee trainee = session.createQuery(getTraineeHql, Trainee.class)
                    .setParameter("userName", traineeUsername)
                    .uniqueResult();
            if (trainee == null) {
                LOGGER.debug("DAO: No trainee found with username '{}'", traineeUsername);
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

            LOGGER.debug("DAO: Retrieved Trainers {} not assigned to trainee '{}'", result.size(), traineeUsername);
            return result;
        } catch (Exception e) {
            LOGGER.error("DAO: Error fetching unassigned trainers for trainee '{}'", traineeUsername);
            String errorMsg=String.format("DAO: Error fetching unassigned trainers for trainee '%s'", traineeUsername);
            throw new DaoException(errorMsg, e);
        }

    }
}
