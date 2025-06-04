package epamlab.spring.gymapp.storage;

import java.util.*;

public abstract class InMemoryStorage<T> {

    private Map<Long, T> data = new HashMap<>();


    public void save(Long id, T obj) {
        data.put(id, obj);

    }

    public T get(Long id) {
        return data.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(data.values());
    }

    public boolean delete(Long id) {
        T obj = data.remove(id);
        return obj != null;
    }
}

