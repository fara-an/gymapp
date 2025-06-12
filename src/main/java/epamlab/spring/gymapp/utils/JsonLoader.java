package epamlab.spring.gymapp.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
public class JsonLoader {
    private final ObjectMapper objectMapper;

    public JsonLoader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> List<T> loadList(InputStream inputStream, Class<T> itemClass) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, itemClass);
        return objectMapper.readValue(inputStream, javaType);
    }
}
