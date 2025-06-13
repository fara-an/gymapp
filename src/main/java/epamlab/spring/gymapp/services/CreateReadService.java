package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.BaseEntity;

import java.io.Serializable;
import java.util.Optional;

public interface CreateReadService<T extends BaseEntity<ID>, ID extends Serializable> {
    T create(T obj);

    T findById(ID id);
}
