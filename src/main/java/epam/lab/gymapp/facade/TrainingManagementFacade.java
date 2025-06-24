package epam.lab.gymapp.facade;

import epam.lab.gymapp.model.Trainee;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.service.interfaces.TraineeService;
import epam.lab.gymapp.service.interfaces.TrainerService;
import epam.lab.gymapp.service.interfaces.TrainingService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TrainingManagementFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;


    public TrainingManagementFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    public void findTraineeById(Long id) {
        traineeService.findById(id);
    }

    public void findTraineeByUsername(Credentials credentials, String username) {
        traineeService.findByUsername(username);
    }

    public void registerTrainee(Trainee trainee) {
        traineeService.createProfile(trainee);
    }

    public void updateTrainee( Trainee trainee) {
        traineeService.updateProfile( trainee);
    }

    public void deleteTrainee(Credentials credentials, String traineeUsername) {
        traineeService.delete(credentials, traineeUsername);
    }

    public List<Training> getTraineeTrainingsBasedOn(Credentials credentials, String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType) {
        return traineeService.getTraineeTrainings(
                credentials,
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType);
    }

    public void toggleActiveStatusTrainee(Credentials credentials, String userName) {
        traineeService.toggleActiveStatus(userName);
    }

    public void changePasswordTrainee(String userName, String oldPassword, String newPassword) {
        traineeService.changePassword(userName, oldPassword, newPassword);
    }


    public void registerTrainer(Trainer trainer) {
        trainerService.createProfile(trainer);
    }

    public void updateTrainer(Trainer trainer) {
        trainerService.updateProfile( trainer);
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

    public void findTrainerById(Long id) {
        trainerService.findById(id);
    }

    public void findTrainerByUsername(String traineeUsername) {
        traineeService.findByUsername(traineeUsername);
    }

    public void toggleActiveStatusTrainer(String userName) {
        trainerService.toggleActiveStatus(userName);
    }

    public void changePasswordTrainer(String userName, String oldPassword, String newPassword) {
        trainerService.changePassword(userName, oldPassword, newPassword);
    }

    public void addTraining(Training training) {
        trainingService.addTraining(training);
    }


}



