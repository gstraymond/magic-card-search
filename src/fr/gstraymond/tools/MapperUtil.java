package fr.gstraymond.tools;

import java.io.IOException;
import java.io.InputStream;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapperUtil<T> {
	
	private ObjectMapper objectMapper;
	private Class<T> clazz;

	public MapperUtil(ObjectMapper objectMapper, Class<T> clazz) {
		this.objectMapper = objectMapper;
		this.clazz = clazz;
	}

	public T read(InputStream stream) {
		try {
			return objectMapper.readValue(stream, clazz);
		} catch (JsonParseException e) {
			Log.e(getClass().getName(), "read", e);
		} catch (JsonMappingException e) {
			Log.e(getClass().getName(), "read", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "read", e);
		}
		
		return null;
	}
	
	public String asJsonString(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			Log.e(getClass().getName(), "asJsonString", e);
		}
		
		return null;
	}
}
