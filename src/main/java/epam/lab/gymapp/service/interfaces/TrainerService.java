package epam.lab.gymapp.service.interfaces;

import epam.lab.gymapp.dto.Credentials;
import epam.lab.gymapp.dto.registration.TrainerRegistrationBody;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dao.interfaces.TrainerDao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainerService extends ProfileOperations<Trainer, TrainerDao, TrainerRegistrationBody> {
    List<Training> getTrainerTrainings(Credentials credentials, String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
     List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);

}
