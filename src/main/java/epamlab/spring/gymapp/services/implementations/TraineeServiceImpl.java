package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TraineeDao;
import epamlab.spring.gymapp.services.TraineeService;
import epamlab.spring.gymapp.util.PasswordGenerator;
import epamlab.spring.gymapp.util.UsernameGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainee;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Trainee createTrainee(String firstName, String lastName, boolean isActive, LocalDateTime birthday, String address, long userId) {
        LOGGER.info("Creating trainee profile {} {}", firstName, lastName);
        String baseUsername=firstName + "." + lastName;
        Trainee trainee = traineeDao.findByUsername(baseUsername);
        boolean usernameExists=trainee!=null;

        if (usernameExists) {
            LOGGER.info("Username '{}' already exists. Generating unique username.", baseUsername);
        }
        String username=UsernameGenerator.generateUsername(firstName, lastName, usernameExists);
        String password = PasswordGenerator.generatePassword();
        Trainee newTrainee = new Trainee(firstName, lastName, username, password, isActive, birthday, address, userId);
        traineeDao.save(newTrainee);
        LOGGER.info("Trainee created successfully with username: '{}', userId: {}", username, userId);
        return trainee;
    }

    @Override
    public Trainee getTrainee(Long id ) {
        Optional<Trainee> optionalTrainee = traineeDao.get(id);
        return optionalTrainee.orElse(null);
    }

    @Override
    public void updateTrainee(long id, Trainee updatedTrainee) {
        LOGGER.info("Attempting to update trainee with ID: {}", id);
        Optional<Trainee> trainee = traineeDao.get(id);
        if (trainee.isPresent()) {
            Trainee t = new Trainee(updatedTrainee.getFirstName(), updatedTrainee.getLastName(),
                    updatedTrainee.getUserName(), updatedTrainee.getPassword(),
                    updatedTrainee.isActive(), updatedTrainee.getBirthday(), updatedTrainee.getAddress(), id);
            traineeDao.update(id, t);
            LOGGER.info("Trainer with ID {} successfully updated.", id);
            return;
        }else {
            LOGGER.warn("Trainer with id {} not found", updatedTrainee.getId());

        }
    }

    public void deleteTrainee(long id) {
        LOGGER.info("Attempting to delete user with ID:{}",id);
        traineeDao.delete(id);
    }


}
