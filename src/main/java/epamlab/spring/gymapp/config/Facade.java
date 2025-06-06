package epamlab.spring.gymapp.config;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.services.TraineeService;
import epamlab.spring.gymapp.services.TrainerService;
import epamlab.spring.gymapp.services.TrainingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import static epamlab.spring.gymapp.model.TrainingType.GROUP;
import static epamlab.spring.gymapp.model.TrainingType.INDIVIDUAL;
@Component
public class Facade {


    TraineeService traineeService;
    TrainerService trainerService;
    TrainingService trainingService;

    @Autowired
    public Facade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    public void  createNewEntities() {
        Trainee trainee = traineeService.createTrainee("John",
                "Doe",
                true,
                LocalDateTime.now().minusYears(24),
                "New York",
                123
        );

        Trainer trainer = trainerService.createTrainer(
                "Janetta",
                "Doe",
                true,
                "Fitness", INDIVIDUAL,
                null,
                13
        );

        Training training = trainingService.create(
                new Training(201,
                123,
                13,
                "Nutrition Basics",
                GROUP,
                LocalDateTime.now().minusDays(5),
                Duration.ofMinutes(120)));

    }

    public void checkLogic(){




    }


}
