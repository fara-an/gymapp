package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.model.Trainer;
import epam.lab.gymapp.model.Training;

import java.time.LocalDateTime;


public interface TrainingService {
    Training addTraining(TrainingAddDto trainingAddDto);

    void deleteTraining(String trainerName, String traineeName, LocalDateTime start);

    Training findTraining(String trainerName, String traineeName, LocalDateTime start);

}
