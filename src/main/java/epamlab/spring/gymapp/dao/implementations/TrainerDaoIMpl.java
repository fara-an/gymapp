package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.dao.interfaces.TrainerDao;
import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
@Repository
public class TrainerDaoIMpl extends BaseDao<Trainer, Long> implements TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoIMpl.class);
    private static final String LOG_FETCH_TRAININGS_START = "DAO: Fetching trainings for trainer '{}', trainee '{}', from {} to {}";
    private static final String LOG_ERROR_FETCH_TRAININGS = "DAO: Error retrieving trainings for trainer '{}', trainee '{}'";
    private static final String LOG_ERROR_FETCH_TRAININGS_TEMPLATE= "DAO: Error retrieving trainings for trainer '%s', trainee '%s'";
    private static final String LOG_FETCH_UNASSIGNED_TRAINERS = "DAO: Fetching trainers not assigned to trainee '{}'";
    private static final String LOG_TRAINEE_NOT_FOUND = "DAO: No trainee found with username '{}'";
    private static final String LOG_UNASSIGNED_TRAINERS_FOUND_SUCCESS = "DAO: Retrieved Trainers {} not assigned to trainee '{}'";
    private static final String LOG_ERROR_FETCH_UNASSIGNED_TRAINERS = "DAO: Error fetching unassigned trainers for trainee '{}'";
    private static final String LOG_ERROR_FETCH_UNASSIGNED_TRAINERS_TEMPLATE = "DAO: Error fetching unassigned trainers for trainee '%s'";



    @Override
    public List<Training> getTrainerTrainings(Credentials credentials,String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername) {
        LOGGER.debug(LOG_FETCH_TRAININGS_START, trainerUsername, traineeUsername, fromDate, toDate);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String hql = """
                    select t
                    from Training t
                    where t.trainer.userProfile.userName = :trainerUsername
                      and t.trainee.userProfile.userName = :traineeUsername
                      and t.trainingDate between :fromDate and :toDate
                    """;

            return session.createQuery(hql, Training.class)
                    .setParameter("trainerUsername", trainerUsername)
                    .setParameter("traineeUsername", traineeUsername)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.debug(LOG_ERROR_FETCH_TRAININGS, trainerUsername, traineeUsername, e);
            String errorMessage = String.format(LOG_ERROR_FETCH_TRAININGS_TEMPLATE, trainerUsername, traineeUsername);
            throw new DaoException(errorMessage, e);
        }
    }

    @Override
    public List<Trainer> trainersNotAssignedToTrainee(String traineeUsername) {
        LOGGER.debug(LOG_FETCH_UNASSIGNED_TRAINERS, traineeUsername);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String getTraineeHql = "from Trainee t where t.userProfile.userName = :userName";
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
