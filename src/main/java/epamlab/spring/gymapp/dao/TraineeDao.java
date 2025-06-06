package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.TraineeDaoInterface;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.storage.TraineeStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao implements TraineeDaoInterface<Trainee> {


    private TraineeStorage traineeStorage;

    public TraineeDao(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Optional<Trainee> get(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    public Trainee findByUsername(String username) {
        List<Trainee> trainees = traineeStorage.getAll();
        return trainees.stream().filter(trainee -> trainee.getUserName().equals(username)).findFirst().orElse(null);
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

}
