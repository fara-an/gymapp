package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TrainerDao;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.services.TrainerService;
import epamlab.spring.gymapp.util.PasswordGenerator;
import epamlab.spring.gymapp.util.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;

import java.util.Optional;


@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TrainerDao trainerDao;

    @Override
    public Trainer createTrainer(String firstName, String lastName, boolean isActive, String specialization, TrainingType trainingType, Training training, long userId) {
        LOGGER.info("Creating trainer profile {} {}", firstName, lastName);
        String baseUsername = firstName + "." + lastName;
        Trainer existingTrainer = trainerDao.findByUsername(baseUsername);
        boolean usernameExists = existingTrainer != null;


        if (usernameExists) {
            LOGGER.info("Username '{}' already exists. Generating unique username.", baseUsername);
        }

        String username = UsernameGenerator.generateUsername(firstName, lastName, usernameExists);
        String password = PasswordGenerator.generatePassword();
        Trainer newTrainer = new Trainer(firstName, lastName, username, password, isActive, specialization, trainingType, userId);
        trainerDao.save(newTrainer);
        return newTrainer;
    }

    @Override
    public void updateTrainer(long id, Trainer updatedTrainer) {
        Optional<Trainer> trainer = trainerDao.get(id);
        if (trainer.isPresent()) {
            Trainer newTrainer = new Trainer(updatedTrainer.getFirstName(),
                    updatedTrainer.getLastName(),
                    updatedTrainer.getUserName(),
                    updatedTrainer.getPassword(),
                    updatedTrainer.isActive(),
                    updatedTrainer.getSpecialization(),
                    updatedTrainer.getTrainingType(),
                    id);
            trainerDao.update(id, newTrainer);
            LOGGER.info("Trainer with id {}  is successfully updated", id);
        }
        LOGGER.warn("Trainer with id {} not found", id);
    }

    @Override
    public Trainer getTrainer(long id) {
        LOGGER.info("Retrieving trainer with ID: {}", id);
        Optional<Trainer> trainerOptional = trainerDao.get(id);
        if (trainerOptional.isPresent()) {
            LOGGER.info("Trainer found: {}", trainerOptional.get().getUserName());
        } else {
            LOGGER.warn("Trainer with ID {} not found.", id);
        }
        return trainerOptional.orElse(null);
    }


}
