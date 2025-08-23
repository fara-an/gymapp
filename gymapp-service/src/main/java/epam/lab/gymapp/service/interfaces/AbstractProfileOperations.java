package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.dao.interfaces.CreateReadUpdateDao;
import epam.lab.gymapp.exceptions.EntityNotFoundException;
import epam.lab.gymapp.utils.PasswordGenerator;
import epam.lab.gymapp.utils.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public abstract class AbstractProfileOperations<
        T extends UserProfile,
        D extends CreateReadUpdateDao<T, Long>> implements ProfileOperations<T, D> {

    Logger LOGGER = LoggerFactory.getLogger(AbstractProfileOperations.class);

    protected PasswordEncoder passwordEncoder;

    public AbstractProfileOperations(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public T createProfile(T item) {
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
        String password = passwordEncoder.encode(PasswordGenerator.generatePassword());

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

    @Override
    @Transactional
    public T updateProfile(T item) {
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


    @Override
    @Transactional(readOnly = true)
    public T findByUsername(String username) {
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

    @Override
    @Transactional(readOnly = true)
    public T findById(Long id) {
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


}
