package epamlab.spring.gymapp.dao;

import java.util.List;
import java.util.Optional;

public interface Dao <T> {

    Optional<T> get(Long id);
    Optional<T> get(String username);
    List<T> getAll();
    void save(T t);
    void update(long id,T t);
    void  delete(long id);
    long findUsernamesStartsWith(String userName);


}
