package com.magic.card.search.commons.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.magic.card.search.commons.log.Log;

import java.io.InputStream;
import java.util.List;

public class MapperUtil<T> {

    private ObjectMapper objectMapper;
    private JavaType javaType;
    private Log log = new Log(this);

    public static <T> MapperUtil<T> fromType(ObjectMapper objectMapper, Class<T> clazz) {
        return new MapperUtil<>(
                objectMapper,
                getTypeFactory(objectMapper).constructType(clazz));
    }

    public static <T> MapperUtil<List<T>> fromCollectionType(ObjectMapper objectMapper, Class<T> clazz) {
        return new MapperUtil<>(
                objectMapper,
                getTypeFactory(objectMapper).constructCollectionType(List.class, clazz));
    }

    private static TypeFactory getTypeFactory(ObjectMapper objectMapper) {
        return objectMapper.getTypeFactory();
    }

    private MapperUtil(ObjectMapper objectMapper, JavaType javaType) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
    }

    public T read(InputStream stream) {
        long now = System.currentTimeMillis();
        try {
            T t = objectMapper.readValue(stream, javaType);
            log.d("read %s took %sms", javaType, System.currentTimeMillis() - now);
            return t;
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public T read(String string) {
        long now = System.currentTimeMillis();
        try {
            T t = objectMapper.readValue(string, javaType);
            log.d("read %s took %sms", javaType, System.currentTimeMillis() - now);
            return t;
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public String asJsonString(Object object) {
        if (object == null) return null;

        long now = System.currentTimeMillis();
        try {
            String s = objectMapper.writeValueAsString(object);
            log.d("to json %s took %sms", object.getClass(), System.currentTimeMillis() - now);
            return s;
        } catch (JsonProcessingException e) {
            log.e("asJsonString", e);
            return null;
        }
    }
}
