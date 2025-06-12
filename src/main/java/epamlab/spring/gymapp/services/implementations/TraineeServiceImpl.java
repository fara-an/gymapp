package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TraineeDao;
import epamlab.spring.gymapp.services.CrudService;
import epamlab.spring.gymapp.utils.PasswordGenerator;
import epamlab.spring.gymapp.utils.UsernameGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainee;
import org.slf4j.Logger;

import java.util.Optional;

@Service
public class TraineeServiceImpl implements CrudService<Trainee, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Trainee create(Trainee trainee) {
        LOGGER.info("Creating trainee profile {} {}", trainee.getFirstName(), trainee.getLastName());

        String username = UsernameGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                userNameToBeChecked -> traineeDao.findByUsername(userNameToBeChecked) != null
        );

        String password = PasswordGenerator.generatePassword();
        Trainee newTrainee = Trainee.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .userName(username)
                .password(password)
                .isActive(trainee.isActive())
                .address(trainee.getAddress())
                .birthday(trainee.getBirthday())
                .build();


        traineeDao.create(newTrainee);
        LOGGER.info("Trainee created successfully with username: '{}', userId: {}", username, trainee.getId());
        return newTrainee;
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
            Trainee t = Trainee.builder()
                    .id(trainee.getId())
                    .firstName(trainee.getFirstName())
                    .lastName(trainee.getLastName())
                    .userName(trainee.getUserName())
                    .password(trainee.getPassword())
                    .isActive(trainee.isActive())
                    .address(trainee.getAddress())
                    .birthday(trainee.getBirthday())
                    .build();
            traineeDao.update(id, t);
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
