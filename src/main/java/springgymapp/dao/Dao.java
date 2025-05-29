package springgymapp.dao;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface Dao <T> {

    Optional<T> get(Long id);
    List<T> getAll();
    void save(T t);
    void update(long id,T t);
    void  delete(T t);


}
