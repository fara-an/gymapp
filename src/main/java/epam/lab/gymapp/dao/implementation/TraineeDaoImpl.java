package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.TraineeDao;
import epam.lab.gymapp.exceptions.DaoException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class TraineeDaoImpl extends BaseDao<Trainee, Long> implements TraineeDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeDaoImpl.class);

    public TraineeDaoImpl( SessionFactory sessionFactory) {
        super(Trainee.class, sessionFactory);
    }

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        LOGGER.debug("DAO: Fetching trainings for, trainee '{}', from {} to {}, trainingType '{}'", traineeUsername, fromDate, toDate, trainingType);
        try {
            Session session = getSessionFactory().getCurrentSession();
            String hql = """
                    select t
                    from Training t
                    where t.trainee.userName = :traineeUsername
                      and t.trainingType.name = :trainingType
                    """;

            List<Training> resultList = session.createQuery(hql, Training.class)
                    .setParameter("traineeUsername", traineeUsername)
                    .setParameter("trainingType", trainingType)
                    .getResultList();

            return resultList.stream()
                    .filter(t -> {
                        LocalDateTime start = t.getTrainingDate();
                        LocalDateTime end = start.plusMinutes((long)(t.getDuration() * 60));
                        return (start.isBefore(toDate) && end.isAfter(fromDate));
                    })
                    .toList();
        } catch (Exception e) {
            LOGGER.debug("DAO: Error retrieving trainings for trainee '{}'", traineeUsername, e);
            String errorMessage = String.format("DAO: Error retrieving trainings for trainee %s", traineeUsername);
            throw new DaoException(errorMessage, e);
        }
    }
}
