package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.annotation.security.RequiresAuthentication;
import epam.lab.gymapp.dto.request.registration.RegistrationDto;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.dao.interfaces.CreateReadUpdateDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.exceptions.ServiceException;
import epam.lab.gymapp.utils.PasswordGenerator;
import epam.lab.gymapp.utils.UsernameGenerator;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfileOperations<
        T extends UserProfile,
        D extends CreateReadUpdateDao<T, Long>,
        R extends RegistrationDto> {

    Logger LOGGER = LoggerFactory.getLogger(ProfileOperations.class);

    D getDao();

    AuthenticationService getAuthService();

    T buildProfile(UserProfile user, T profile);

    void updateProfileSpecificFields(T existing, T item);

    @RequiresAuthentication
    @Transactional
    default T createProfile(T item) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Creating entity: {} {}",
                serviceName,
                item.getFirstName(),
                item.getLastName()
        );

        String username = UsernameGenerator.generateUsername(
                item.getFirstName(),
                item.getLastName(),
                usernameToBeChecked -> getDao().findByUsername(item.getUserName()).isPresent());
        String password = PasswordGenerator.generatePassword();

        UserProfile newUser = UserProfile.builder()
                .userName(username)
                .firstName(item.getFirstName())
                .lastName(item.getLastName())
                .password(password)
                .isActive(true)
                .build();


        T newEntity = buildProfile(newUser, item);
        T created = getDao().create(newEntity);

        LOGGER.debug("{}: SERVICE - Created entity: {}", serviceName, created);
        return created;
    }

    @RequiresAuthentication
    @Transactional
    default T updateProfile(T item) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Updating entity ID: {}", serviceName, item.getId());

        T existing = findById(item.getId());
        Optional.ofNullable(item.getFirstName()).ifPresent(existing::setFirstName);
        Optional.ofNullable(item.getLastName()).ifPresent(existing::setLastName);
        existing.setIsActive(item.getIsActive());

        updateProfileSpecificFields(existing, item);

        T updated = getDao().update(existing);
        LOGGER.debug("{}: SERVICE - Updated entity ID: {}", serviceName, existing.getId());
        return updated;
    }


    @RequiresAuthentication
    @Transactional(readOnly = true)
    default T findByUsername(String username) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Searching entity by username: {}", serviceName, username);
        T item = getDao().findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
                    LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
                    return new EntityNotFoundException(msg);
                });

        LOGGER.debug("{}: SERVICE - Entity found by username: {}", serviceName, username);
        return item;
    }

    @RequiresAuthentication
    @Transactional(readOnly = true)
    default T findById(Long id) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Searching entity by ID: {}", serviceName, id);

        T item = getDao().findByID(id)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with ID '%s' not found.", serviceName, id);
                    LOGGER.error("{}: SERVICE ERROR - Entity with ID '{}' not found", serviceName, id);
                    return new EntityNotFoundException(msg);
                });

        LOGGER.debug("{}: SERVICE - Entity found by ID: {}", serviceName, id);
        return item;
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
            throw new ServiceException(msg, e);
        }
    }

    @RequiresAuthentication
    @Transactional
    default String changePassword(String username, String oldPassword, String newPassword) {
        String serviceName = getClass().getSimpleName();
        LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);

        T entity = getDao().findByUsername(username)
                .orElseThrow(() -> {
                    String msg = String.format("%s: Entity with username '%s' not found.", serviceName, username);
                    LOGGER.error("{}: SERVICE ERROR - Entity with username '{}' not found", serviceName, username);
                    return new EntityNotFoundException(msg);
                });

        if (oldPassword.equals(entity.getPassword())) {
            entity.setPassword(newPassword);
        }

        try {
            Session session = getDao().getSessionFactory().getCurrentSession();
            session.merge(entity);
            LOGGER.debug("{}: SERVICE - Changing password for user: {}", serviceName, username);
            return newPassword;

        } catch (Exception e) {
            LOGGER.error("{}: SERVICE ERROR - Error changing password for user: {}: {}", serviceName, username, e.getMessage(), e);

            throw new ServiceException("%s: Error changing password for '%s'", e);
        }
    }
}
