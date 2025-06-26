package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class TrainingDaoImpl extends BaseDao<Training, Long> implements TrainingDao {

    public TrainingDaoImpl( SessionFactory sessionFactory) {
        super(Training.class, sessionFactory);
    }


    @Override
    public boolean existsTrainerConflict(Long trainerId, LocalDateTime newStart, LocalDateTime newEnd) {
        Session session = getSessionFactory().getCurrentSession();

        String hql = """
            select count(t.id)
            from   Training t
            where  t.trainer.id = :trainerId
              and  t.trainingDate < :newEnd
              and  (t.trainingDate + t.duration) > :newStart
            """;

        Long cnt = session.createQuery(hql, Long.class)
                .setParameter("trainerId", trainerId)
                .setParameter("newStart", newStart)
                .setParameter("newEnd",   newEnd)
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
                  and  t.trainingDate < :newEnd
                  and  t.trainingDate + function('INTERVAL', concat(t.duration, ' minutes')) > :newStart
  """;
        Long cnt = session.createQuery(hql, Long.class)
                .setParameter("traineeId", traineeId)
                .setParameter("newStart", newStart)
                .setParameter("newEnd",   newEnd)
                .uniqueResult();

        return cnt != null && cnt > 0;
    }
}
