package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.storage.TrainingStorage;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Training;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDao implements Dao<Training> {

    private TrainingStorage trainingStorage;

    public TrainingDao(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Optional<Training> get(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public Optional<Training> get(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Training> getAll() {
        return trainingStorage.getAll();
    }

    @Override
    public void save(Training training) {
        trainingStorage.save(training.getId(), training);
    }

    @Override
    public void update(long id, Training training) {
        trainingStorage.save(id, training);
    }


    public void delete(Training training) {
        trainingStorage.delete(training.getId());

    }

    @Override
    public void delete(long id) {
        trainingStorage.delete(id);

    }

    @Override
    public long findUsernamesStartsWith(String userName) {
        throw new UnsupportedOperationException("Method is not supported in" + getClass().getName());

    }
}
