package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.dao.interfaces.TraineeDao;
import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Training;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class TraineeDaoImpl extends BaseDao<Trainee, Long> implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDaoImpl.class);

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        LOGGER.debug("DAO: Fetching trainings for, trainee '{}', from {} to {}, trainingType '{}'", traineeUsername, fromDate, toDate, trainingType);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String hql = """
                    select t
                    from Training t
                    where t.trainee.userEntity.userName = :traineeUsername
                      and t.trainingDate between :fromDate and :toDate
                      and t.trainingType.name = :trainingType
                    """;

            return session.createQuery(hql, Training.class)
                    .setParameter("traineeUsername", traineeUsername)
                    .setParameter("fromDate", fromDate)
                    .setParameter("toDate", toDate)
                    .setParameter("trainingType", trainingType)
                    .getResultList();
        } catch (Exception e) {
            LOGGER.debug("DAO: Error retrieving trainings for trainee '{}'", traineeUsername, e);
            String errorMessage = String.format("DAO: Error retrieving trainings for trainee '{}'", traineeUsername);
            throw new DaoException(errorMessage, e);
        }
    }
}
