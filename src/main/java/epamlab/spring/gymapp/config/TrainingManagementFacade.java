package epamlab.spring.gymapp.config;

import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;
import epamlab.spring.gymapp.services.CreateReadService;
import epamlab.spring.gymapp.services.CreateReadUpdateService;
import epamlab.spring.gymapp.services.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrainingManagementFacade {


   private CrudService<Trainee, Long> traineeService;
   private CreateReadUpdateService<Trainer, Long> trainerService;
   private CreateReadService<Training, Long> trainingService;

    @Autowired
    public TrainingManagementFacade(CrudService<Trainee, Long> traineeService,  CreateReadUpdateService<Trainer,Long> trainerService, CreateReadService<Training,Long> trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }


    public void organizeTraining(Training training, long trainerId, long traineeId){
        Training newTraining = trainingService.create(training);
        TrainingType trainingType = newTraining.getTrainingType();
        Trainee trainee=traineeService.findById(traineeId);
        Trainer trainer = trainerService.findById(trainerId);
        if (trainer.getTrainingType().equals(trainingType)) {
            newTraining.setTrainer(trainer);
            newTraining.setTrainee(trainee);
        }
    }




}
