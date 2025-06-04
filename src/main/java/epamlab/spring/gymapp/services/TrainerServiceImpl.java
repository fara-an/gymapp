package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.dao.TrainerDao;
import epamlab.spring.gymapp.services.serviceInterfaces.TrainerService;
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

    private TrainerDao trainerDao;

    private DotSeparatedUsernameAndPasswordGeneration usernameAndPasswordGenerator;


    @Autowired
    public TrainerServiceImpl(TrainerDao trainerDao, DotSeparatedUsernameAndPasswordGeneration dotSeparatedUsernameAndPasswordGeneration) {
        this.trainerDao = trainerDao;
        this.usernameAndPasswordGenerator = dotSeparatedUsernameAndPasswordGeneration;
    }

    @Override
    public void createTrainer(String firstName, String lastName, boolean isActive, String specialization, TrainingType trainingType, Training training, long userId) {
        String userName = firstName + "." + lastName;
        long numberOfSameUsernames = trainerDao.findUsernamesStartsWith(userName);
        usernameAndPasswordGenerator.generateUsername(firstName, lastName, numberOfSameUsernames);
        String password = usernameAndPasswordGenerator.generatePassword();
        Trainer newTrainer = new Trainer(firstName, lastName, userName, password, isActive, specialization, trainingType, training, userId);
        trainerDao.save(newTrainer);
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
                    updatedTrainer.getTraining(),
                    id);
            trainerDao.update(id, newTrainer);
            LOGGER.info("Trainer with id {}  is successfully updated", id);
        }
        LOGGER.warn("Trainer with id {} not found", id);
    }

    @Override
    public Trainer getTrainer(long id) {
        Optional<Trainer> trainerOptional = trainerDao.get(id);
        return trainerOptional.orElse(null);
    }


}
