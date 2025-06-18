package epamlab.spring.gymapp.dao.implementations;

import epamlab.spring.gymapp.dao.interfaces.AuthenticationDao;
import epamlab.spring.gymapp.exceptions.DaoException;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.UserProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationDaoImpl implements AuthenticationDao {

    public static final String HQL_USERNAME_PASSWORD =
            "FROM UserProfile u WHERE u.userName = :username AND u.password = :password";

    private final Logger LOGGER = LoggerFactory.getLogger(AuthenticationDaoImpl.class);
    String LOG_VALIDATE_TEMPLATE = "Validating credentials for username: {}";
    String LOG_ERROR_TEMPLATE = "Error validating credentials for {} – {}";
    String ERROR_MSG_TEMPLATE = "Error validating credentials for %s";

    private final SessionFactory sessionFactory;

    public AuthenticationDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean validateCredentials(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        LOGGER.debug(LOG_VALIDATE_TEMPLATE, username);

        try {
            Session currentSession = sessionFactory.getCurrentSession();
            UserProfile userEntity = currentSession.createQuery(HQL_USERNAME_PASSWORD, UserProfile.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return userEntity != null;
        } catch (Exception e) {
            String errorMessage = String.format(ERROR_MSG_TEMPLATE, username);
            LOGGER.error(LOG_ERROR_TEMPLATE, username, e.getMessage());
            throw new DaoException(errorMessage, e);
        }
    }
}
