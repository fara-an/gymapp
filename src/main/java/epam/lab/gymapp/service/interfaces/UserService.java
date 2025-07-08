package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.annotation.security.RequiresAuthentication;
import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.exceptions.DaoException;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.InvalidCredentialsException;
import epam.lab.gymapp.model.UserProfile;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;


public interface UserService<T extends UserProfile,
        D extends CreateReadDao<T, Long>> {

    D getDao();

    Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @RequiresAuthentication
    @Transactional
    default void changePassword(String username, String oldPassword, String newPassword) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);

        T entity = getDao().findByUsername(username)
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
            Session session = getDao().getSessionFactory().getCurrentSession();
            session.merge(entity);
            LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);

        } catch (Exception e) {
            LOGGER.error("{}: SERVICE ERROR - Error changing password for user: {}: {}", serviceName, username, e.getMessage(), e);
            throw new DaoException("%s: Error changing password for '%s'", e);
        }
    }


    @RequiresAuthentication
    @Transactional
    default void toggleActiveStatus(String username) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Toggling active status for username: {}", serviceName, username);

        T entity = getDao().findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
                    LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
                    return new EntityNotFoundException(msg);
                });
        entity.setIsActive(!entity.getIsActive());

        try {
            Session session = getDao().getSessionFactory().getCurrentSession();
            session.merge(entity);
            LOGGER.debug("{}: SERVICE - Active status toggled for username: {}", serviceName, username);

        } catch (Exception e) {
            LOGGER.error("{}: SERVICE ERROR - Error toggling active status for username: {}: {}", serviceName, username, e.getMessage(), e);
            String msg = String.format("%s: Error toggling active status for '%s'", serviceName, username);
            throw new DaoException(msg, e);
        }
    }
}
