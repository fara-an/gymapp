package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.BaseEntity;

import java.io.Serializable;

public interface CrudService<T extends BaseEntity<ID>, ID extends Serializable> extends CreateReadUpdateService<T, ID> {
    void delete(ID id);
}
