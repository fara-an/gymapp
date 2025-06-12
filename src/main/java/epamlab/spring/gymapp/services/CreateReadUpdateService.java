package epamlab.spring.gymapp.services;

import epamlab.spring.gymapp.model.BaseEntity;

public interface CreateReadUpdateService<T extends BaseEntity<ID>,ID> extends CreateReadService<T,ID>{
    void update(ID id, T item);
}
