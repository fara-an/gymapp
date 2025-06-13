package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.TrainerDao;
import epamlab.spring.gymapp.services.CreateReadUpdateService;
import epamlab.spring.gymapp.utils.PasswordGenerator;
import epamlab.spring.gymapp.utils.UsernameGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.UserEntity;
import epamlab.spring.gymapp.model.TrainingType;

import java.util.Optional;

@Service
public class TrainerServiceImpl implements CreateReadUpdateService<Trainer, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    @Autowired
    private TrainerDao trainerDao;

    @Transactional
    public Trainer createProfile(String firstName, String lastName, TrainingType specialization) {
        LOGGER.info("Creating trainer profile for {} {}", firstName, lastName);

        // Create UserEntity
        UserEntity<Long> userEntity = UserEntity.<Long>builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();

        // Generate username and password
        String username = UsernameGenerator.generateUsername(
                firstName,
                lastName,
                userNameToBeChecked -> trainerDao.findByUsername(userNameToBeChecked) != null
        );
        String password = PasswordGenerator.generatePassword();

        userEntity.setUserName(username);
        userEntity.setPassword(password);

        // Create Trainer
        Trainer trainer = Trainer.builder()
                .userEntity(userEntity)
                .specialization(specialization)
                .build();

        // Save to database
        trainerDao.create(trainer);

        LOGGER.info("Trainer profile created successfully with username: {}", username);
        return trainer;
    }

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        LOGGER.info("Creating trainer profile {} {}", trainer.getUserEntity().getFirstName(), trainer.getUserEntity().getLastName());
        String username = UsernameGenerator.generateUsername(
                trainer.getUserEntity().getFirstName(),
                trainer.getUserEntity().getLastName(),
                userNameToBeChecked -> trainerDao.findByUsername(userNameToBeChecked) != null
        );
        String password = PasswordGenerator.generatePassword();

        trainer.getUserEntity().setUserName(username);
        trainer.getUserEntity().setPassword(password);

        trainerDao.create(trainer);
        return trainer;
    }

    @Override
    @Transactional
    public void update(Long id, Trainer trainer) {
        Optional<Trainer> oldTrainer = trainerDao.findById(id);
        if (oldTrainer.isPresent()) {
            trainerDao.update(id, trainer);
            LOGGER.info("Trainer with id {} is successfully updated", id);
        } else {
            LOGGER.warn("Trainer with id {} not found", id);
        }
    }

    @Override
    public Trainer findById(Long id) {
        LOGGER.debug("Retrieving trainer with ID: {}", id);
        Optional<Trainer> trainerOptional = trainerDao.findById(id);
        if (trainerOptional.isPresent()) {
            LOGGER.debug("Trainer found: {}", trainerOptional.get().getUserEntity().getUserName());
        } else {
            LOGGER.error("Trainer with ID {} not found.", id);
        }
        return trainerOptional.orElse(null);
    }

    public Trainer findByUsername(String username) {
        LOGGER.debug("Retrieving trainer with username: {}", username);
        return trainerDao.findByUsername(username);
    }
}
