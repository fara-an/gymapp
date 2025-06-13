package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.dao.interfaces.TrainerDao;
import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class TrainerDaoIMpl extends BaseDao<Trainer, Long> implements TrainerDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerDaoIMpl.class);

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String traineeUsername) {
        LOGGER.debug("DAO: Fetching trainings for trainer '{}', trainee '{}', from {} to {}", trainerUsername, traineeUsername, fromDate, toDate);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String hql = """
                    select t
                    from Training t
                    where t.trainer.userEntity.userName = :trainerUsername
                      and t.trainee.userEntity.userName = :traineeUsername
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
           String errorMessage= String.format("DAO: Error retrieving trainings for trainer '{}', trainee '{}'", trainerUsername, traineeUsername);
            throw new DaoException(errorMessage,e);
        }
    }

    @Override
    public List<Trainer> trainersWithoutTrainees(String traineeUsername) {
        return List.of();
    }
}
