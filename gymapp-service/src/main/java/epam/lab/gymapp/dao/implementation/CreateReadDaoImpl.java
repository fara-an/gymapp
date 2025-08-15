package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.base.BaseDao;
import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.model.UserProfile;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class CreateReadDaoImpl extends BaseDao<UserProfile,Long> implements CreateReadDao<UserProfile, Long> {

    public CreateReadDaoImpl( SessionFactory sessionFactory) {
        super(UserProfile.class, sessionFactory);
    }
}
