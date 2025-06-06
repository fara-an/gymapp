package epamlab.spring.gymapp.dao.interfaces;

import java.util.Optional;

public interface TrainingDaoInterface<Training>{

    Optional<Training> get(Long id);
    void save(Training t);
}
