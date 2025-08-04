package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public class TrainingDaoImpl extends BaseDao<Training, Long> implements TrainingDao {

    public TrainingDaoImpl(SessionFactory sessionFactory) {
        super(Training.class, sessionFactory);
    }


    @Override
    public boolean existsTrainerConflict(Long trainerId, LocalDateTime newStart, LocalDateTime newEnd) {
        Session session = getSessionFactory().getCurrentSession();
        String hql = """
                select count(t.id)
                from   Training t
                where  t.trainer.id = :trainerId
                  and  t.trainingDateStart < :newEnd
                  and  t.trainingDateEnd > :newStart
                """;

        Long cnt = session.createQuery(hql, Long.class)
                .setParameter("trainerId", trainerId)
                .setParameter("newStart", newStart)
                .setParameter("newEnd", newEnd)
                .uniqueResult();

        return cnt != null && cnt > 0;
    }

    @Override
    public boolean existsTraineeConflict(Long traineeId, LocalDateTime newStart, LocalDateTime newEnd) {
        Session session = getSessionFactory().getCurrentSession();
        String hql = """
                select count(t.id)
                from   Training t
                where  t.trainee.id = :traineeId
                  and  t.trainingDateStart < :newEnd
                  and  t.trainingDateEnd > :newStart
                """;

        Long cnt = session.createQuery(hql, Long.class)
                .setParameter("traineeId", traineeId)
                .setParameter("newStart", newStart)
                .setParameter("newEnd", newEnd)
                .uniqueResult();

        return cnt != null && cnt > 0;
    }

    @Transactional
    public Training findTraining(String trainerUsername, String traineeUsername, LocalDateTime startTime) {

        Session session = getSessionFactory().getCurrentSession();

        String sql = """
                    SELECT trn.*
                    FROM training trn
                    JOIN trainer t ON trn.trainer_id = t.user_id
                    JOIN userprofile trainer_profile ON t.user_id = trainer_profile.user_id
                    JOIN trainee te ON trn.trainee_id = te.user_id
                    JOIN userprofile trainee_profile ON te.user_id = trainee_profile.user_id
                    WHERE trainer_profile.user_name = :trainerUsername
                      AND trainee_profile.user_name = :traineeUsername
                      AND trn.training_date_start = :startTime
                """;

        return session.createNativeQuery(sql, Training.class)
                .setParameter("trainerUsername", trainerUsername)
                .setParameter("traineeUsername", traineeUsername)
                .setParameter("startTime", startTime)
                .uniqueResultOptional()
                .orElseThrow(() ->
                     new EntityNotFoundException("Training with traineeUsername '%s';, trainerUsername '%s', starTime '%s' not found" .formatted(trainerUsername, traineeUsername, startTime)));



    }

    @Override
    @Transactional
    public void deleteTraining(Training training) {
        Session session = getSessionFactory().getCurrentSession();
         session.createMutationQuery("DELETE FROM Training t WHERE t.id = :id")
                .setParameter("id", training.getId())
                .executeUpdate();
    }

    @Transactional
    public Trainer updateTrainingTrainer(Long trainingId,
                                         String traineeUsername,
                                         String newTrainerUsername) {
        Session session = getSessionFactory().getCurrentSession();

        LOGGER.debug("SERVICE – Re‑assigning training id={} from trainee='{}' to trainer='{}'",
                trainingId, traineeUsername, newTrainerUsername);
        LOGGER.trace("QUERY – Loading Training entity id={}", trainingId);

        Training training = session.createQuery("""
                        SELECT  tr
                        FROM    Training tr
                                JOIN FETCH tr.trainer
                                JOIN FETCH tr.trainee
                        WHERE   tr.id = :id
                        """, Training.class)
                .setParameter("id", trainingId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Training id=%d not found" .formatted(trainingId)));

        if (!training.getTrainee().getUserName().equals(traineeUsername)) {
            System.out.println(training.getTrainee().getUserName());
            throw new EntityNotFoundException("Training id=%d does not belong to trainee '%s'"
                    .formatted(trainingId, traineeUsername));
        }

        LOGGER.trace("CHECK – Training id={} confirmed for trainee='{}'", trainingId, traineeUsername);
        LOGGER.trace("QUERY – Loading Trainer entity userName='{}'", newTrainerUsername);

        Trainer newTrainer = session.createQuery("""
                        SELECT  t
                        FROM    Trainer t
                        WHERE   t.userName = :uname
                        """, Trainer.class)
                .setParameter("uname", newTrainerUsername)
                .getResultStream()
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Trainer '%s' not found" .formatted(newTrainerUsername)));

        if (!newTrainer.equals(training.getTrainer())) {
            LOGGER.debug("UPDATE – Changing trainer on Training id={} from '{}' to '{}'",
                    trainingId, training.getTrainer().getUserName(), newTrainerUsername);
            training.setTrainer(newTrainer);
        } else {
            LOGGER.debug("NO‑OP – Training id={} already assigned to trainer '{}'",
                    trainingId, newTrainerUsername);
        }
        return newTrainer;

    }
}




