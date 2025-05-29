package springgymapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import springgymapp.dao.TrainerDao;
import springgymapp.model.Trainer;
import springgymapp.model.Training;
import springgymapp.model.TrainingType;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Random;

public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeService.class);
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new SecureRandom();

    private TrainerDao trainerDao;

    @Autowired
    public TrainerService(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public void create(String firstName, String lastName, boolean isActive, String specialization, TrainingType trainingType, Training training, long userId) {
        String password = generatePassword();
        String userName = generateUsername(firstName, lastName);
        Trainer newTrainer = new Trainer(firstName, lastName, userName, password, isActive, specialization, trainingType, training, userId);
        trainerDao.save(newTrainer);
    }

    public void update(long id, Trainer updatedTrainer) {
        Optional<Trainer> trainer = trainerDao.get(id);
        if (trainer.isPresent()) {
            Trainer newTrainer = new Trainer(updatedTrainer.getFirstName(),
                    updatedTrainer.getLastName(),
                    updatedTrainer.getUserName(),
                    updatedTrainer.getPassword(),
                    updatedTrainer.isActive(),
                    updatedTrainer.getSpecialization(),
                    updatedTrainer.getTrainingType(),
                    updatedTrainer.getTraining(),
                    id);
            trainerDao.update(id, newTrainer);
            logger.info("Trainer with id {}  is successfully updated", id);
        }
        logger.warn("Trainer with id {} not found", id);
    }

    public void delete(Trainer trainer) {
        trainerDao.delete(trainer);
    }


    private String generatePassword() {
        StringBuilder password = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String userName = baseUsername;
        int suffix = 1;
        while (trainerDao.get(userName).isPresent()) {
            userName = baseUsername + suffix;
            suffix++;
        }
        return userName;
    }
}
