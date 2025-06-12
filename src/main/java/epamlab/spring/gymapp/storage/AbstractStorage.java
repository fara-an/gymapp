package epamlab.spring.gymapp.storage;

import epamlab.spring.gymapp.model.BaseEntity;
import epamlab.spring.gymapp.utils.JsonLoader;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Setter
public abstract class AbstractStorage<T extends BaseEntity<ID>, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStorage.class);
    private Map<ID, T> data = new HashMap<>();
    private String jsonFilePath;
    private JsonLoader jsonLoader;
    private Class<T> itemClass;


    @PostConstruct
    private void init() {
        if (jsonFilePath == null || jsonFilePath.trim().isEmpty()) {
            LOGGER.debug("jsonFilePath not set for {} – can not find json to map objects to",
                    getClass().getSimpleName());
            return;
        }

        if (itemClass == null) {
            LOGGER.debug("Could not determine the type of  class to map objects to");
            throw new IllegalStateException("Could not determine the type of  class to map objects to" + getClass().getSimpleName());
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(jsonFilePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File to load data was not found " + jsonFilePath);
            }
            List<T> ietmList = jsonLoader.loadList(inputStream, itemClass);
            for (T item : ietmList) {
                data.put(item.getId(), item);
                LOGGER.debug("Loaded {} item with ID={}", getClass().getSimpleName(), item.getId());
            }
            LOGGER.info("Successfully loaded {}  records", ietmList.size());

        } catch (IOException e) {
            LOGGER.error("Failed to initialize {} from {}: {}", getClass().getSimpleName(), jsonFilePath, e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize storage from " + jsonFilePath, e);

        }
    }

    public void save(ID id, T obj) {
        data.put(id, obj);

    }

    public T get(ID id) {
        return data.get(id);
    }

    public List<T> getAll() {
        return new ArrayList<>(data.values());
    }

    public boolean delete(ID id) {
        T obj = data.remove(id);
        return obj != null;
    }


}

