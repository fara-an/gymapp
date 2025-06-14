package epamlab.spring.gymapp.config;

import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.*;
import epamlab.spring.gymapp.services.interfaces.TraineeService;
import epamlab.spring.gymapp.services.interfaces.TrainerService;
import epamlab.spring.gymapp.services.interfaces.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TrainingManagementFacade {

    TraineeService traineeService;
    TrainerService trainerService;
    TrainingService trainingService;


    public TrainingManagementFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public  void findTraineeById(Credentials credentials, Long id){
        traineeService.findById(credentials,id);
    }

    public void findTraineeByUsername(Credentials credentials, String username){
        traineeService.findByUsername(credentials,username);
    }

    public void registerTrainee(Trainee trainee) {
        traineeService.createProfile(trainee);
    }

    public void updateTrainee(Credentials credentials, Trainee trainee) {
        traineeService.updateProfile(credentials, trainee);
    }

    public void deleteTrainee(Credentials credentials, String traineeUsername) {
        traineeService.delete(credentials, traineeUsername);
    }

    public void getTraineeTrainingsBasedOn(Credentials credentials, String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        traineeService.getTraineeTrainings(
                credentials,
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType);
    }

    public void toggleActiveStatusTrainee(Credentials credentials, String userName){
        traineeService.toggleActiveStatus(credentials, userName);
    }

    public void  changePasswordTrainee(Credentials credentials, String userName){
        traineeService.changePassword(credentials,userName);
    }


    public void registerTrainer(Trainer trainer) {
        trainerService.createProfile(trainer);
    }

    public void updateTrainer(Credentials credentials, Trainer trainer) {
        trainerService.updateProfile(credentials, trainer);
    }


    public void getTrainerTrainingsBasedOn(Credentials credentials, String trainerUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        trainerService.getTrainerTrainings(
                credentials,
                trainerUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType);
    }

    public  void findTrainerById(Credentials credentials, Long id){
        trainerService.findById(credentials,id);
    }

    public void findTrainerByUsername(Credentials credentials, String traineeUsername){
        traineeService.findByUsername(credentials, traineeUsername);
    }



    public void toggleActiveStatusTrainer(Credentials credentials, String userName){
        trainerService.toggleActiveStatus(credentials, userName);
    }

    public void  changePasswordTrainer(Credentials credentials, String userName){
        trainerService.changePassword(credentials,userName);
    }


}



