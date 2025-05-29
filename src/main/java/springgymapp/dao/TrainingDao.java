package springgymapp.dao;

import org.springframework.stereotype.Repository;
import springgymapp.model.Training;

import java.util.List;
import java.util.Optional;
@Repository
public class TrainingDao implements Dao<Training> {

    private TrainingStorage trainingStorage;


    @Override
    public Optional<Training> get(Long id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }

    @Override
    public List<Training> getAll() {
        return (List<Training>) trainingStorage.getAll();
    }

    @Override
    public void save(Training training) {
        trainingStorage.save(training.getId(), training);
    }

    @Override
    public void update(long id, Training training) {
        trainingStorage.save(id, training);
    }


    @Override
    public void delete(Training training) {
        trainingStorage.delete(training.getId());

    }
}
