package fr.gstraymond.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class MapperUtil<T> {

    private ObjectMapper objectMapper;
    private Class<T> clazz;
    private Log log = new Log(this);

    public MapperUtil(ObjectMapper objectMapper, Class<T> clazz) {
        this.objectMapper = objectMapper;
        this.clazz = clazz;
    }

    public T read(InputStream stream) {
        try {
            return objectMapper.readValue(stream, clazz);
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public T read(String string) {
        try {
            return objectMapper.readValue(string, clazz);
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.e("asJsonString", e);
        }

        return null;
    }
}
