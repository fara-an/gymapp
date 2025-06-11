package epamlab.spring.gymapp.dao.interfaces;

import epamlab.spring.gymapp.model.BaseEntity;

public interface CrudDao<T extends BaseEntity<ID>, ID> extends CreateReadUpdateDao<T, ID>{
    void delete(ID id);
}
