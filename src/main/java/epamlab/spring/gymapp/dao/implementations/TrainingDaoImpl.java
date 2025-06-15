package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.base.BaseDao;
import epamlab.spring.gymapp.dao.interfaces.CreateDao;
import epamlab.spring.gymapp.dao.interfaces.ReadDao;
import epamlab.spring.gymapp.model.Training;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDaoImpl extends BaseDao<Training, Long> implements
        CreateDao<Training, Long>,
        ReadDao<Training, Long> {



    public TrainingDaoImpl( SessionFactory sessionFactory) {
        super(Training.class, sessionFactory);
    }
}
