package epamlab.spring.gymapp.services.serviceInterfaces;

import epamlab.spring.gymapp.model.Training;

public interface TrainingService {

    void create(Training training);

    Training get(Training training);
}
