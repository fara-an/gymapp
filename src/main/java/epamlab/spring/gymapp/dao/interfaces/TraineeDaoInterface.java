package epamlab.spring.gymapp.dao.interfaces;


import java.util.Optional;

public interface TraineeDaoInterface<Trainee> {


    Optional<Trainee> get(Long id);
    void save(Trainee t);
    void update(long id,Trainee t);
    void  delete(long id);
}
