package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TrainerDao;
import epamlab.spring.gymapp.services.CreateReadUpdateService;
import epamlab.spring.gymapp.utils.PasswordGenerator;
import epamlab.spring.gymapp.utils.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;

import java.util.Optional;


@Service
public class TrainerServiceImpl implements CreateReadUpdateService<Trainer, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);

    @Autowired
    private TrainerDao trainerDao;

    @Override
    public Trainer create(Trainer trainer) {
        LOGGER.info("Creating trainer profile {} {}", trainer.getFirstName(), trainer.getLastName());
        String username = UsernameGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                userNameToBeChecked -> trainerDao.findByUsername(userNameToBeChecked) != null
        );
        String password = PasswordGenerator.generatePassword();
        Trainer newTrainer = Trainer.builder()
                .id(trainer.getId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .userName(username)
                .password(password)
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .trainingType(trainer.getTrainingType())
                .build();

        trainerDao.create(newTrainer);
        return newTrainer;
    }

    @Override
    public void update(Long id, Trainer trainer) {
        Optional<Trainer> oldTrainer = trainerDao.findById(id);
        if (oldTrainer.isPresent()) {
            Trainer newTrainer = Trainer.builder()
                    .id(trainer.getId())
                    .firstName(trainer.getFirstName())
                    .lastName(trainer.getLastName())
                    .userName(trainer.getUserName())
                    .password(trainer.getPassword())
                    .isActive(trainer.isActive())
                    .specialization(trainer.getSpecialization())
                    .trainingType(trainer.getTrainingType())
                    .build();

            trainerDao.update(id, newTrainer);
            LOGGER.info("Trainer with id {}  is successfully updated", id);
        }
        LOGGER.warn("Trainer with id {} not found", id);
    }

    @Override
    public Trainer findById(Long id) {
        LOGGER.debug("Retrieving trainer with ID: {}", id);
        Optional<Trainer> trainerOptional = trainerDao.findById(id);
        if (trainerOptional.isPresent()) {
            LOGGER.debug("Trainer found: {}", trainerOptional.get().getUserName());
        } else {
            LOGGER.error("Trainer with ID {} not found.", id);
        }
        return trainerOptional.orElse(null);
    }


}
