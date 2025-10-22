package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.model.Training;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


public interface TrainingService {
    ResponseEntity<?> addTraining(TrainingAddDto trainingAddDto);

    ResponseEntity<?> deleteTraining(String trainerName, String traineeName, LocalDateTime start);

    Training findTraining(String trainerName, String traineeName, LocalDateTime start);

}
