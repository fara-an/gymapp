package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.TrainingTypeDao;
import epam.lab.gymapp.model.TrainingType;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingTypeDaoImpl extends BaseDao<TrainingType,Long> implements TrainingTypeDao<TrainingType,Long> {

    public TrainingTypeDaoImpl( SessionFactory sessionFactory) {
        super(TrainingType.class, sessionFactory);
    }


}
