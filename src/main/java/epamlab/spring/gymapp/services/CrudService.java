package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.BaseEntity;

public interface CrudService<T extends BaseEntity<ID>, ID> extends CreateReadUpdateService<T, ID> {
    void delete(ID id);
}
