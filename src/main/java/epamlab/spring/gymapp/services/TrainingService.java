package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.Training;

public interface TrainingService {

    Training create(Training training);

    Training get(Training training);
}
