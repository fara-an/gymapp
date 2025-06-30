package epam.lab.gymapp.service.implementation;

import epam.lab.gymapp.annotation.security.RequiresAuthentication;
import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.model.TrainingType;
import epam.lab.gymapp.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private static final String SERVICE_NAME = "TrainingServiceImpl";

    private final TrainingDao trainingDao;
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingTypeService trainingTypeService;

    public TrainingServiceImpl(TrainingDao trainingDao, TrainerService trainerService, TraineeService traineeService, TrainingTypeService trainingTypeService) {
        this.trainingDao = trainingDao;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingTypeService = trainingTypeService;
    }
    @RequiresAuthentication
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
            throw new IllegalArgumentException(
                    "Trainee already has a session that overlaps with this time window"
            );
        }

        if (trainingDao.existsTrainerConflict(trainer.getId(), start, end)) {
            throw new IllegalArgumentException(
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

        Training created = trainingDao.create(newTraining);
        LOGGER.debug(SERVICE_NAME + " - Created training: {}", created);
        return created;

    }



    private void validateTrainingType(String actual, String expected) {
        if (!actual.equals(expected)) {
            String errorMessage = String.format(SERVICE_NAME + ": Trainer specialization '%s' does not match required '%s'", actual, expected);
            LOGGER.error(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }


}
