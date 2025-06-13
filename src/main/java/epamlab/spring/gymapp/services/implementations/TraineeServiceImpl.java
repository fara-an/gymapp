package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.model.UserEntity;
import epamlab.spring.gymapp.services.CrudService;
import epamlab.spring.gymapp.utils.PasswordGenerator;
import epamlab.spring.gymapp.utils.UsernameGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainee;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements CrudService<Trainee, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TraineeDao traineeDao;

    public Trainee createProfile(String firstName, String lastName, LocalDateTime localDateTime, String address) {
        UserEntity<Long> userEntity = UserEntity.<Long>builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();
        String username = UsernameGenerator.generateUsername(firstName, lastName, userNameToBeChecked -> traineeDao.findByUsername(userNameToBeChecked) != null);
        String password = PasswordGenerator.generatePassword();
        userEntity.setUserName(username);
        userEntity.setPassword(password);

        Trainee trainee = Trainee.builder()
                .userEntity(userEntity)
                .birthday(localDateTime)
                .address(address)
                .build();

        traineeDao.create(trainee);
        LOGGER.info("Trainee profile created successfully with username: {}", username);
        return trainee;


    }

    @Override
    public Trainee create(Trainee trainee) {
        LOGGER.info("Creating trainee profile {} {}", trainee.getUserEntity().getFirstName(), trainee.getUserEntity().getLastName());

        String username = UsernameGenerator.generateUsername(
                trainee.getUserEntity().getFirstName(),
                trainee.getUserEntity().getLastName(),
                userNameToBeChecked -> traineeDao.findByUsername(userNameToBeChecked) != null
        );

        String password = PasswordGenerator.generatePassword();

        trainee.getUserEntity().setUserName(username);
        trainee.getUserEntity().setPassword(password);

        traineeDao.create(trainee);
        LOGGER.info("Trainee created successfully with username: '{}', userId: {}", username, trainee.getId());
        return trainee;
    }

    @Override
    public Trainee findById(Long id) {
        LOGGER.debug("Searching for trainee with ID: {}", id);
        Trainee trainee = traineeDao.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Trainee with ID {} not found!", id);
                    return new IllegalArgumentException("Trainee with ID " + id + " not found.");
                });
        LOGGER.debug("Trainee found successfully: {}", trainee);
        return trainee;
    }

    @Override
    public void update(Long id, Trainee trainee) {
        LOGGER.info("Attempting to update trainee with ID: {}", id);
        Optional<Trainee> oldTrainee = traineeDao.findById(id);
        if (oldTrainee.isPresent()) {
            traineeDao.update(id, trainee);
            LOGGER.info("Trainer with ID {} successfully updated.", id);
        } else {
            LOGGER.warn("Trainer with id {} not found", trainee.getId());

        }
    }

    public void delete(Long id) {
        LOGGER.info("Attempting to delete user with ID:{}", id);
        traineeDao.delete(id);
        LOGGER.debug("Trainee with ID {} deleted successfully.", id);
    }


}
