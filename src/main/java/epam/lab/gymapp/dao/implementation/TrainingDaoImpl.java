package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDaoImpl extends BaseDao<Training, Long> implements CreateReadDao<Training,Long> {

    public TrainingDaoImpl( SessionFactory sessionFactory) {
        super(Training.class, sessionFactory);
    }
}
