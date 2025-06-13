package epamlab.spring.gymapp.dao;

import epamlab.spring.gymapp.dao.interfaces.CreateReadUpdateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import epamlab.spring.gymapp.model.Trainer;
import epamlab.spring.gymapp.storage.TrainerStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDao implements CreateReadUpdateDao<Trainer, Long> {

    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public void create(Trainer trainer) {
        trainerStorage.save(trainer.getId(), trainer);
    }

    @Override
    public void update(Long id, Trainer trainer) {
        trainerStorage.save(id, trainer);
    }

    public Trainer findByUsername(String username) {
        List<Trainer> trainers = trainerStorage.getAll();
        return trainers.stream().filter(trainer -> trainer.getUserEntity().getUserName().equals(username)).findFirst().orElse(null);
    }

}
