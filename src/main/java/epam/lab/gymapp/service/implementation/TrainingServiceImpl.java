package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.interfaces.CreateReadDao;
import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.service.interfaces.AuthenticationService;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private static final String SERVICE_NAME = "TrainingServiceImpl";

    private final CreateReadDao<Training, Long> trainingDao;
    private final AuthenticationService authenticationService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public TrainingServiceImpl(CreateReadDao<Training, Long> trainingDao, AuthenticationService authenticationService, TraineeService traineeService, TrainerService trainerService) {
        this.trainingDao = trainingDao;
        this.authenticationService = authenticationService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    @Override
    @Transactional
    public Training addTraining(Credentials credentials, Training training) {
        LOGGER.debug(SERVICE_NAME + " - Starting training creation: {}", training.getTrainingName());
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
        LOGGER.debug(SERVICE_NAME + " - Created training: {}",created);
        return created;
    }

   private void validateTrainingType(String actual, String expected) {
        if (!actual.equals(expected)) {
            String errorMessage = String.format( SERVICE_NAME + ": Trainer specialization '%s' does not match required '%s'", actual, expected);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }
}
