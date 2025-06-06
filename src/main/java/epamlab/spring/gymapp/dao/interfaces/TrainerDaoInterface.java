package epamlab.spring.gymapp.dao.interfaces;

import java.util.Optional;

public interface TrainerDaoInterface <Trainer>{
    Optional<Trainer> get(Long id);
    void save(Trainer t);
    void update(long id,Trainer t);

}
