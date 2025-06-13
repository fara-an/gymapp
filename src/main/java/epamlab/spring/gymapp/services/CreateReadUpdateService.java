package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.BaseEntity;

import java.io.Serializable;

public interface CreateReadUpdateService<T extends BaseEntity<ID>,ID extends Serializable> extends CreateReadService<T,ID>{
    void update(ID id, T item);
}
