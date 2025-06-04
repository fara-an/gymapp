package epamlab.spring.gymapp.dao;

import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.storage.TrainerStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao implements Dao<Trainer> {

    private TrainerStorage trainerStorage;

    public TrainerDao(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Optional<Trainer> get(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    public Optional<Trainer> get(String username) {
        List<Trainer> trainers = (List<Trainer>) trainerStorage.getAll();
        return trainers.stream().filter(t -> t.getUserName().equals(username)).findFirst();

    }

    @Override
    public List<Trainer> getAll() {
        return trainerStorage.getAll();

    }

    @Override
    public void save(Trainer trainer) {
        trainerStorage.save(trainer.getUserId(), trainer);
    }

    @Override
    public void update(long id, Trainer trainer) {
        trainerStorage.save(id, trainer);
    }

    @Override
    public void delete(long id) {
        trainerStorage.delete(id);
    }

    public long findUsernamesStartsWith(String username) {
        List<Trainer> trainers = trainerStorage.getAll();
        return trainers.stream().map(t -> t.getUserName()).filter(s -> s.contains(username)).count();
    }
}
