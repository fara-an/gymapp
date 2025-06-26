package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.model.Training;


public interface TrainingService {
    Training addTraining(TrainingAddDto trainingAddDto);
}
