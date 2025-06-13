package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;
import epamlab.spring.gymapp.model.UserEntity;
import epamlab.spring.gymapp.services.AuthenticationService;
import epamlab.spring.gymapp.services.CreateReadUpdateService;
import epamlab.spring.gymapp.utils.PasswordGenerator;
import epamlab.spring.gymapp.utils.UsernameGenerator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements CreateReadUpdateService<Trainer, Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private TrainingDao trainingDao;

    @Autowired
    private AuthenticationService authenticationService;

    @Transactional
    public Trainer createProfile(String firstName, String lastName, TrainingType specialization) {
        LOGGER.info("Creating trainer profile for {} {}", firstName, lastName);
        
        UserEntity<Long> userEntity = UserEntity.<Long>builder()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .build();

        String username = UsernameGenerator.generateUsername(
                firstName,
                lastName,
                userNameToBeChecked -> trainerDao.findByUserEntity_UserName(userNameToBeChecked) != null
        );
        String password = PasswordGenerator.generatePassword();
        
        userEntity.setUserName(username);
        userEntity.setPassword(password);

        Trainer trainer = Trainer.builder()
                .userEntity(userEntity)
                .specialization(specialization)
                .build();

        trainerDao.save(trainer);
        LOGGER.info("Trainer profile created successfully with username: {}", username);
        return trainer;
    }

    @Override
    @Transactional
    public Trainer create(Trainer trainer) {
        LOGGER.info("Creating trainer profile {} {}", 
            trainer.getUserEntity().getFirstName(), 
            trainer.getUserEntity().getLastName());

        String username = UsernameGenerator.generateUsername(
                trainer.getUserEntity().getFirstName(),
                trainer.getUserEntity().getLastName(),
                userNameToBeChecked -> trainerDao.findByUserEntity_UserName(userNameToBeChecked) != null
        );
        String password = PasswordGenerator.generatePassword();
        
        trainer.getUserEntity().setUserName(username);
        trainer.getUserEntity().setPassword(password);

        return trainerDao.save(trainer);
    }

    @Override
    @Transactional
    public void update(Long id, Trainer trainer) {
        Optional<Trainer> existingTrainer = trainerDao.findById(id);
        if (existingTrainer.isPresent()) {
            trainer.setId(id);
            trainerDao.save(trainer);
            LOGGER.info("Trainer with id {} is successfully updated", id);
        } else {
            LOGGER.warn("Trainer with id {} not found", id);
            throw new EntityNotFoundException("Trainer not found with id: " + id);
        }
    }

    @Override
    public Trainer findById(Long id) {
        LOGGER.debug("Retrieving trainer with ID: {}", id);
        return trainerDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found with id: " + id));
    }

    public Trainer findByUsername(String username) {
        LOGGER.debug("Retrieving trainer with username: {}", username);
        return trainerDao.findByUserEntity_UserName(username);
    }

    @Transactional
    public void activateTrainer(String username) {
        Trainer trainer = findByUsername(username);
        if (trainer != null) {
            trainer.getUserEntity().setIsActive(true);
            trainerDao.save(trainer);
            LOGGER.info("Trainer {} activated successfully", username);
        } else {
            throw new EntityNotFoundException("Trainer not found with username: " + username);
        }
    }

    @Transactional
    public void deactivateTrainer(String username) {
        Trainer trainer = findByUsername(username);
        if (trainer != null) {
            trainer.getUserEntity().setIsActive(false);
            trainerDao.save(trainer);
            LOGGER.info("Trainer {} deactivated successfully", username);
        } else {
            throw new EntityNotFoundException("Trainer not found with username: " + username);
        }
    }

    public List<Training> getTrainerTrainings(String username, LocalDateTime fromDate, 
            LocalDateTime toDate, String traineeName) {
        Trainer trainer = findByUsername(username);
        if (trainer == null) {
            throw new EntityNotFoundException("Trainer not found with username: " + username);
        }
        
        return trainingDao.findByTrainerAndDateRangeAndTraineeName(
                trainer, fromDate, toDate, traineeName);
    }

    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return trainerDao.findUnassignedTrainers(traineeUsername);
    }
}
