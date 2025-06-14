package epamlab.spring.gymapp.services.implementations;

import epamlab.spring.gymapp.dao.interfaces.CreateDao;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.services.interfaces.AuthenticationService;
import epamlab.spring.gymapp.services.interfaces.TraineeService;
import epamlab.spring.gymapp.services.interfaces.TrainerService;
import epamlab.spring.gymapp.services.interfaces.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epamlab.spring.gymapp.model.Training;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private static final String SERVICE_NAME = "TrainingServiceImpl";
    private static final String LOG_ADD_START = SERVICE_NAME + " - Starting training creation: {}";
    private static final String LOG_ADD_SUCCESS = SERVICE_NAME + " - Created training: {}";
    private static final String ERR_INVALID_SPECIALIZATION =
            SERVICE_NAME + ": Trainer specialization '%s' does not match required '%s'";

    private final CreateDao<Training, Long> trainingDao;
    private final AuthenticationService authenticationService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingServiceImpl(CreateDao<Training, Long> trainingDao, AuthenticationService authenticationService, TraineeService traineeService, TrainerService trainerService) {
        this.trainingDao = trainingDao;
        this.authenticationService = authenticationService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Override
    @Transactional
    public Training addTraining(Credentials credentials, Training training) {
        LOGGER.debug(LOG_ADD_START, training.getTrainingName());
        authenticationService.authenticateUser(credentials);

        Long trainerId = training.getTrainer().getId();
        Trainer trainer = trainerService.findById(credentials, trainerId);

        Long traineeId = training.getTrainee().getId();
        Trainee trainee = traineeService.findById(credentials, traineeId);
        validateTrainingType(trainer.getSpecialization().getName(), training.getTrainingType().getName());
        Training newTraining = Training.builder()
                .id(training.getId())
                .trainee(trainee)
                .trainer(trainer)
                .duration(training.getDuration())
                .trainingDate(training.getTrainingDate())
                .trainingType(training.getTrainingType())
                .build();

        Training created = trainingDao.create(newTraining);
        LOGGER.debug(LOG_ADD_SUCCESS,created);
        return created;
    }

   private void validateTrainingType(String actual, String expected) {
        if (!actual.equals(expected)) {
            String errorMessage = String.format(ERR_INVALID_SPECIALIZATION, actual, expected);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }
}
