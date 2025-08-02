package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.UserProfile;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private static final String SERVICE_NAME = "TrainerServiceImpl";
    private final TrainerDao trainerDao;
    private final AuthenticationService authenticationService;

    public TrainerServiceImpl(TrainerDao trainerDao, AuthenticationService authenticationService) {
        this.trainerDao = trainerDao;
        this.authenticationService = authenticationService;
    }

    @Override
    @Transactional
    public List<Training> getTrainerTrainings(Credentials credentials, String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        LOGGER.debug(SERVICE_NAME+" - Fetching trainings for trainer {} with [from={}, to={}, trainee={}]", trainerName, fromDate, toDate, traineeUsername);
        List<Training> trainerTrainings = trainerDao.getTrainerTrainings(credentials, traineeUsername, fromDate, toDate, traineeUsername);
        LOGGER.debug(SERVICE_NAME+" - Retrieved {} trainings for trainer {}", trainerTrainings.size(), trainerName);
        return trainerTrainings;
    }

    @Override
    @Transactional
    public List<Trainer> trainersNotAssignedToTrainee(String traineeUsername) {
        LOGGER.debug(SERVICE_NAME+" - Initiating search for unassigned trainers (without trainees).");
        List<Trainer> trainers = trainerDao.trainersNotAssignedToTrainee(traineeUsername);
        LOGGER.debug(SERVICE_NAME+"- Search completed. Found {} unassigned trainers.", trainers.size());
        return trainers;
    }

    @Override
    public TrainerDao getDao() {
        return trainerDao;
    }

    @Override
    public AuthenticationService getAuthService() {
        return authenticationService;
    }


    @Override
    public Trainer buildProfile(UserProfile user, Trainer profile) {
        return Trainer.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userName(user.getUserName())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .specialization(profile.getSpecialization())
                .build();
    }

    @Override
    public void updateProfileSpecificFields(Trainer existing, Trainer item) {
        Optional.ofNullable(existing.getSpecialization())
                .ifPresent(newSpecialization -> existing.setSpecialization(newSpecialization));

    }
}
