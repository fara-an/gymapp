package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.dao.interfaces.TrainerDao;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface TrainerService extends ProfileOperations<Trainer, TrainerDao> {

    List<Training> getTrainerTrainings(String traineeUsername, LocalDateTime fromDate, LocalDateTime toDate, String trainerName, String trainingType);

    List<Trainer> trainersNotAssignedToTrainee(String traineeUsername);


}
