package fr.gstraymond.biz;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import android.widget.ProgressBar;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.gstraymond.magicsearch.model.request.Request;
import fr.gstraymond.magicsearch.model.response.SearchResult;

public class ElasticSearchClient { 

	private static final String ENCODING = "UTF-8";
	private URL url;
	private HttpClient httpClient;
	private ObjectMapper objectMapper;
	
	public ElasticSearchClient(URL url) {
		super();
		this.url = url;
		this.httpClient = new DefaultHttpClient();
		this.objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
				.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	public SearchResult process(SearchOptions options, ProgressBar progressBar) {
		Request request = new Request(options);
		try {
			String queryAsJson = objectMapper.writeValueAsString(request);
			Log.d(getClass().getName(), "\n" + queryAsJson);
			
			String query = URLEncoder.encode(queryAsJson, ENCODING);
			HttpGet getRequest = new HttpGet(url.toString() + "?source=" + query);
			long now = System.currentTimeMillis();
			HttpResponse response = httpClient.execute(getRequest);
			Log.i(getClass().getName(), "\thttp client execute " + (System.currentTimeMillis() - now) + "ms");
			progressBar.setProgress(66);
			return parse(response.getEntity().getContent(), progressBar);
		} catch (ClientProtocolException e) {
			Log.e(getClass().getName(), "process", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "process", e);
		}
		return null;
	}

	private SearchResult parse(InputStream stream, ProgressBar progressBar) {
		SearchResult searchResult = null;
		long now = System.currentTimeMillis();
		try {
			searchResult = objectMapper.readValue(stream, SearchResult.class);
		} catch (JsonParseException e) {
			Log.e(getClass().getName(), "parse", e);
		} catch (JsonMappingException e) {
			Log.e(getClass().getName(), "parse", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "parse", e);
		} finally {
			Log.i(getClass().getName(), "\tparse took " + (System.currentTimeMillis() - now) + "ms");
			progressBar.setProgress(100);
		}
		return searchResult;
	}
}
