package epamlab.spring.gymapp.dao.base;

import epamlab.spring.gymapp.model.BaseEntity;
import lombok.Getter;
import org.hibernate.SessionFactory;
@Getter
public abstract class BaseDao<T extends BaseEntity<ID>,ID> {
   public Class<T> entityClass;
   public SessionFactory sessionFactory;

   public BaseDao(Class<T> entityClass, SessionFactory sessionFactory) {
      this.entityClass = entityClass;
      this.sessionFactory = sessionFactory;
   }
}
