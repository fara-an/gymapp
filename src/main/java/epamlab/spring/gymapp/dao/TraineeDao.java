package epamlab.spring.gymapp.dao;

import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.storage.TraineeStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao implements Dao<Trainee> {


    private TraineeStorage traineeStorage;

    public TraineeDao(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Optional<Trainee> get(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    public Optional<Trainee> get(String username) {
        List<Trainee> trainees = traineeStorage.getAll();
        return trainees.stream().filter(trainee -> trainee.getUserName().equals(username)).findFirst();

    }

    @Override
    public List<Trainee> getAll() {
        return traineeStorage.getAll();
    }

    @Override
    public void save(Trainee trainee) {
        traineeStorage.save(trainee.getUserId(), trainee);

    }

    @Override
    public void update(long id, Trainee updatedTrainee) {
        traineeStorage.save(id, updatedTrainee);
    }

    @Override
    public void delete(long id) {
        traineeStorage.delete(id);
    }

    public long findUsernamesStartsWith(String username) {
        List<Trainee> trainers = traineeStorage.getAll();
        return trainers.stream().map(t -> t.getUserName()).filter(s -> s.contains(username)).count();
    }
}
