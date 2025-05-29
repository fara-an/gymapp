package springgymapp.dao;

import org.springframework.stereotype.Repository;
import springgymapp.model.Trainee;
import springgymapp.model.Trainer;

import java.util.List;
import java.util.Optional;
@Repository
public class TrainerDao implements Dao<Trainer> {

    private TrainerStorage trainerStorage;

    @Override
    public Optional<Trainer> get(Long id) {
       return Optional.ofNullable(trainerStorage.get(id));
    }

    public Optional<Trainer> get(String username) {
        List<Trainer> trainers= (List<Trainer>) trainerStorage.getAll();
        return trainers.stream().filter(t -> t.getUserName().equals(username)).findFirst();

    }

    @Override
    public List<Trainer> getAll() {
       return(List<Trainer>) trainerStorage.getAll();

    }

    @Override
    public void save(Trainer trainer) {
     trainerStorage.save(trainer.getUserID(), trainer);
    }

    @Override
    public void update(long id, Trainer trainer) {
        trainerStorage.save(id, trainer);
    }

    @Override
    public void delete(Trainer trainer) {
      trainerStorage.delete(trainer.getUserID());
    }
}
