package epam.lab.gymapp.service.interfaces;


import epam.lab.gymapp.model.Training;
import epam.lab.gymapp.dto.Credentials;

public interface TrainingService  {
     Training addTraining(Credentials credentials, Training training);
}
