package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.BaseEntity;

import java.util.Optional;

public interface CreateReadDao<T extends BaseEntity<ID>, ID> {
    void create(T obj);

    Optional<T> findById(ID id);
}
