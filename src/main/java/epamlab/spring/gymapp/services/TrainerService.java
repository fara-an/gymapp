package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.model.Training;
import epamlab.spring.gymapp.model.TrainingType;

public interface TrainerService {

    Trainer createTrainer(String firstName, String lastName, boolean isActive, String specialization, TrainingType trainingType, Training training, long userId);

    void updateTrainer(long id, Trainer updatedTrainer);

    Trainer getTrainer(long id);
}
