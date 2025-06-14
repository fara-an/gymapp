package epamlab.spring.gymapp.services.interfaces;


import epamlab.spring.gymapp.dto.Credentials;
import epamlab.spring.gymapp.model.Training;

public interface TrainingService  {
     Training addTraining(Credentials credentials, Training training);
}
