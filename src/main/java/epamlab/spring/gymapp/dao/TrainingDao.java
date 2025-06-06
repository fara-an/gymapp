package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.TrainingDaoInterface;
import epamlab.spring.gymapp.storage.TrainingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Training;

import java.util.Optional;

@Repository
public class TrainingDao implements TrainingDaoInterface<Training> {

    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Optional<Training> get(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public void save(Training training) {
        trainingStorage.save(training.getId(), training);
    }

}
