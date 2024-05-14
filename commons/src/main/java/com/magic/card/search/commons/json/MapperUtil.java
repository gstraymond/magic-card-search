package com.magic.card.search.commons.json;

import com.magic.card.search.commons.log.Log;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import okio.Okio;

public class MapperUtil<T> {

    private JsonAdapter<T> jsonAdapter;
    private Log log = new Log(this);

    public static <T> MapperUtil<T> fromType(Moshi moshi, Class<T> clazz) {
        return new MapperUtil<>(moshi.adapter(clazz));
    }

    public static <T> MapperUtil<List<T>> fromCollectionType(Moshi moshi, Class<T> clazz) {
        ParameterizedType type = Types.newParameterizedType(List.class, clazz);
        return new MapperUtil<>(moshi.<List<T>>adapter(type));
    }

    private MapperUtil(JsonAdapter<T> jsonAdapter) {
        this.jsonAdapter = jsonAdapter;
    }

    public T read(InputStream stream) {
        //long now = System.currentTimeMillis();
        try {
            T t = jsonAdapter.fromJson(Okio.buffer(Okio.source(stream)));
            //log.d("read %s took %sms", jsonAdapter, System.currentTimeMillis() - now);
            return t;
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public T read(String string) {
        //long now = System.currentTimeMillis();
        try {
            T t = jsonAdapter.fromJson(string);
            //log.d("read %s took %sms", jsonAdapter, System.currentTimeMillis() - now);
            return t;
        } catch (Exception e) {
            log.e("read", e);
        }

        return null;
    }

    public String asJsonString(T t) {
        if (t == null) return null;

        //long now = System.currentTimeMillis();
        try {
            String s = jsonAdapter.toJson(t);
            //log.d("to json %s took %sms", t.getClass(), System.currentTimeMillis() - now);
            return s;
        } catch (Exception e) {
            log.e("asJsonString", e);
            return null;
        }
    }
}
