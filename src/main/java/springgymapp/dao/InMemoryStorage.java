package springgymapp.dao;

import java.util.*;

public class InMemoryStorage <T>{

    protected Map<Long, T > data = new HashMap<>();


    public void   save(Long id, T obj){
     data.put(id, obj);

    }

    public T get(Long id){
      return   data.get(id);
    }

    public List<T> getAll(){
        return new ArrayList<>( data.values());
    }

    public boolean delete(Long id){
      T obj= data.remove(id);
      return obj != null;
    }
}

