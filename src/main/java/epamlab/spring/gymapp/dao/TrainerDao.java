package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.TrainerDaoInterface;
import epamlab.spring.gymapp.model.Trainee;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.storage.TrainerStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao implements TrainerDaoInterface<Trainer> {

    private TrainerStorage trainerStorage;

    public TrainerDao(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Optional<Trainer> get(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public void save(Trainer trainer) {
        trainerStorage.save(trainer.getId(), trainer);
    }

    @Override
    public void update(long id, Trainer trainer) {
        trainerStorage.save(id, trainer);
    }

    public Trainer findByUsername(String username) {
        List<Trainer> trainers = trainerStorage.getAll();
        return trainers.stream().filter(trainer -> trainer.getUserName().equals(username)).findFirst().orElse(null);
    }

}
