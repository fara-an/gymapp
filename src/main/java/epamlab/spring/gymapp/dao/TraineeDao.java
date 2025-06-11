package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.CrudDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainee;
import epamlab.spring.gymapp.storage.TraineeStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDao implements CrudDao<Trainee, Long> {

    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    public Trainee findByUsername(String username) {
        List<Trainee> trainees = traineeStorage.getAll();
        return trainees.stream().filter(trainee -> trainee.getUserName().equals(username)).findFirst().orElse(null);
    }

    @Override
    public void create(Trainee trainee) {
        traineeStorage.save(trainee.getId(), trainee);

    }

    @Override
    public void update(Long id, Trainee updatedTrainee) {
        traineeStorage.save(id, updatedTrainee);
    }

    @Override
    public void delete(Long id) {
        traineeStorage.delete(id);
    }

}
