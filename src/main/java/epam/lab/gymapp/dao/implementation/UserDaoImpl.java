package epam.lab.gymapp.dao.implementation;

import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.dao.interfaces.UserDao;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.model.UserProfile;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

    CreateReadDao<UserProfile, Long> dao;

    public UserDaoImpl(CreateReadDao<UserProfile, Long> dao) {
        this.dao = dao;
    }

    @Transactional
    public UserProfile findByUsername(String username) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Finding  user with username: {}", serviceName, username);

        UserProfile userProfile = dao.findByUsername(username).orElseThrow(() -> {
            String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
            LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
            return new EntityNotFoundException(msg);
        });
        return userProfile;
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);

        UserProfile entity = dao.findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
                    LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
                    return new EntityNotFoundException(msg);
                });

        if (!oldPassword.equals(entity.getPassword())) {
            throw new InvalidCredentialsException(username);
        }
        entity.setPassword(newPassword);
        try {
            Session session = dao.getSessionFactory().getCurrentSession();
            session.merge(entity);
            LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);

        } catch (Exception e) {
            LOGGER.error("{}: SERVICE ERROR - Error changing password for user: {}: {}", serviceName, username, e.getMessage(), e);
            throw new DaoException("%s: Error changing password for '%s'", e);
        }
    }


    @Transactional
    public void toggleActiveStatus(String username) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Toggling active status for username: {}", serviceName, username);

        UserProfile entity = dao.findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
                    LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
                    return new EntityNotFoundException(msg);
                });
        entity.setIsActive(!entity.getIsActive());

        try {
            Session session = dao.getSessionFactory().getCurrentSession();
            session.merge(entity);
            LOGGER.debug("{}: SERVICE - Active status toggled for username: {}", serviceName, username);

        } catch (Exception e) {
            LOGGER.error("{}: SERVICE ERROR - Error toggling active status for username: {}: {}", serviceName, username, e.getMessage(), e);
            String msg = String.format("%s: Error toggling active status for '%s'", serviceName, username);
            throw new DaoException(msg, e);
        }
    }

}
