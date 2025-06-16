package epamlab.spring.gymapp.services.interfaces;

import epamlab.spring.gymapp.dao.interfaces.TrainerDao;
import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainerService extends ProfileOperations<Trainer, TrainerDao> {
    List<Training> getTrainerTrainings(Credentials credentials,String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);
     List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);

}
