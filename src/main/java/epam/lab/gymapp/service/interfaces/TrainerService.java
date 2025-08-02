package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.annotation.security.RequiresAuthentication;
import epam.lab.gymapp.dto.request.login.Credentials;
import epam.lab.gymapp.dto.request.registration.TrainerRegistrationBody;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainerService extends ProfileOperations<Trainer, TrainerDao, TrainerRegistrationBody> {
    List<Training> getTrainerTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);

    @RequiresAuthentication
    List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);

}
