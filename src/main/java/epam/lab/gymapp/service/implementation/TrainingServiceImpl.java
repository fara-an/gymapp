package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.dto.request.trainerWorkloadRequest.TrainerWorkloadRequest;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.exceptions.UserInputException;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingService;
import epam.lab.gymapp.service.interfaces.TrainingTypeService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import io.github.resilience4j.circuitbreaker.annotation.*;

import java.time.LocalDateTime;
import java.util.function.Supplier;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private static final String SERVICE_NAME = "TrainingServiceImpl";

    private final TrainingDao trainingDao;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;
    private final TrainerWorkloadClientService trainerWorkloadClientService;

    public TrainingServiceImpl(TrainingDao trainingDao, TrainerService trainerService, TraineeService traineeService, TrainingTypeService trainingTypeService,TrainerWorkloadClientService trainerWorkloadClientService) {
        this.trainingDao = trainingDao;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingTypeService = trainingTypeService;
        this.trainerWorkloadClientService = trainerWorkloadClientService;

    }

    @Override
    @Transactional
    public Training addTraining(TrainingAddDto trainingAddDto) {
        LOGGER.debug(SERVICE_NAME + " - Starting training creation: {}", trainingAddDto.getTrainingName());

        Trainer trainer = trainerService.findByUsername(trainingAddDto.getTrainerUserName());
        Trainee trainee = traineeService.findByUsername(trainingAddDto.getTraineeUserName());

        validateTrainingType(trainer.getSpecialization().getName(), trainingAddDto.getTrainingType());

        LocalDateTime start = trainingAddDto.getTrainingDateStart();
        LocalDateTime end = start.plusMinutes(trainingAddDto.getDuration());


        if (trainingDao.existsTraineeConflict(trainee.getId(), start, end)) {
            throw new UserInputException(
                    "Trainee already has a session that overlaps with this time window"
            );
        }

        if (trainingDao.existsTrainerConflict(trainer.getId(), start, end)) {
            throw new UserInputException(
                    "Trainer already has a session that overlaps with this time window"
            );
        }

        TrainingType trainingType = trainingTypeService.findByName(trainingAddDto.getTrainingType());


        Training newTraining = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingName(trainingAddDto.getTrainingName())
                .duration(trainingAddDto.getDuration())
                .trainingDateStart(start)
                .trainingDateEnd(end)
                .trainingType(trainingType)
                .build();

        Training createdTraining = trainingDao.create(newTraining);
        trainerWorkloadClientService.callToTrainerWorkloadService(createdTraining, "ADD");

        LOGGER.debug(SERVICE_NAME + " - Created training: {}", createdTraining);
        return createdTraining;

    }


    private void validateTrainingType(String actual, String expected) {
        if (!actual.equals(expected)) {
            String errorMessage = String.format(SERVICE_NAME + ": Trainer specialization '%s' does not match required '%s'", actual, expected);
            LOGGER.error(errorMessage);
            throw new UserInputException(errorMessage);
        }
    }

    @Override
    public void deleteTraining(String trainerUsername, String traineeUsername, LocalDateTime startTime) {
        LOGGER.debug(SERVICE_NAME + " - Deleting training ");
        Training training = findTraining(trainerUsername, traineeUsername, startTime);
        LOGGER.debug("Training with id{}, trainerUsername {}, traineeUsername {}", training.getId(), training.getTrainer().getUserName(), training.getTrainee().getUserName());
        trainingDao.deleteTraining(training);
        trainerWorkloadClientService.callToTrainerWorkloadService(training, "DELETE");


    }


    @Override
    public Training findTraining(String trainerName, String traineeName, LocalDateTime start) {
        LOGGER.debug(SERVICE_NAME + " - Finding training ");
        Training training = trainingDao.findTraining(trainerName, traineeName, start);
        LOGGER.debug("Training with id {} is found", training.getId());
        return training;
    }
}
