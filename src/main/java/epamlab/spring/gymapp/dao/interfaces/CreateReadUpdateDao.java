package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.BaseEntity;
import epamlab.spring.gymapp.model.Trainer;

import java.util.Optional;

public interface CreateReadUpdateDao<T extends BaseEntity<ID>, ID> extends CreateReadDao<T, ID> {
    void update(ID id, T item);
}
