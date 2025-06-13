package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.AuthenticationDao;
import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.model.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationDaoImpl implements AuthenticationDao {

    public static final String HQL_USERNAME_PASSWORD =
            "FROM UserEntity u WHERE u.userName = :username AND u.password = :password";

    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDaoImpl.class);
    String LOG_VALIDATE_TEMPLATE = "Validating credentials for username: {}";
    String LOG_ERROR_TEMPLATE = "Error validating credentials for {} – {}";
    String ERROR_MSG_TEMPLATE = "Error validating credentials for %s";

    private final SessionFactory sessionFactory;

    public AuthenticationDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        LOGGER.debug(LOG_VALIDATE_TEMPLATE, username);

        try {
            Session currentSession = sessionFactory.getCurrentSession();
            UserEntity userEntity = currentSession.createQuery(HQL_USERNAME_PASSWORD, UserEntity.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return userEntity != null;
        }catch (Exception e){
            String errorMessage = String.format(ERROR_MSG_TEMPLATE, username);
            LOGGER.error(LOG_ERROR_TEMPLATE, username, e.getMessage());
            throw new DaoException(errorMessage, e);
        }
    }
}
