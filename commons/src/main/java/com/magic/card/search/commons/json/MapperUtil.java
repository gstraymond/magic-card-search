package com.magic.card.search.commons.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magic.card.search.commons.log.Log;

import java.io.InputStream;
import java.util.List;

public class MapperUtil<T> {

    private ObjectMapper objectMapper;
    private JavaType javaType;
    private Log log = new Log(this);

    public MapperUtil(ObjectMapper objectMapper, JavaType javaType) {
        this.objectMapper = objectMapper;
        this.javaType = javaType;
    }

    public MapperUtil(ObjectMapper objectMapper, Class clazz) {
        this.objectMapper = objectMapper;
        this.javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
    }

    public T read(InputStream stream) {
        long now = System.currentTimeMillis();
        try {
            T t = objectMapper.readValue(stream, javaType);
            log.d(String.format("read [%s] took %sms", javaType, System.currentTimeMillis() - now));
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
            log.d(String.format("read [%s] took %sms", javaType, System.currentTimeMillis() - now));
            return t;
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public String asJsonString(Object object) {
        long now = System.currentTimeMillis();
        try {
            String s = objectMapper.writeValueAsString(object);
            log.d(String.format("to json took %sms", System.currentTimeMillis() - now));
            return s;
        } catch (JsonProcessingException e) {
            log.e("asJsonString", e);
        }

        return null;
    }
}
