package epamlab.spring.gymapp.config;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;
import epamlab.spring.gymapp.services.TraineeService;
import epamlab.spring.gymapp.services.TrainerService;
import epamlab.spring.gymapp.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingManagementFacade {


   private TraineeService traineeService;
   private TrainerService trainerService;
   private TrainingService trainingService;

    @Autowired
    public TrainingManagementFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    public void organizeTraining(Training training, long trainerId, long traineeId){
        Training newTraining = trainingService.create(training);
        TrainingType trainingType = newTraining.getTrainingType();
        Trainee trainee=traineeService.getTrainee(traineeId);
        Trainer trainer = trainerService.getTrainer(trainerId);
        if (trainer.getTrainingType().equals(trainingType)) {
            newTraining.setTrainer(trainer);
            newTraining.setTrainee(trainee);
        }
    }




}
