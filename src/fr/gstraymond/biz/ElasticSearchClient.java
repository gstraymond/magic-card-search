package fr.gstraymond.biz;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.magicsearch.model.request.Request;
import fr.gstraymond.magicsearch.model.response.SearchResult;
import fr.gstraymond.tools.DisplaySizeUtil;

public class ElasticSearchClient { 
    
	private static final String CONTENT_ENCODING = "Content-Encoding";
	private static final String ACCEPT_ENCODING = "Accept-Encoding";
	private static final String GZIP = "gzip";
	private static final String ENCODING = "UTF-8";
	private URL url;
	private HttpClient httpClient;
	private ObjectMapper objectMapper;
	
	public ElasticSearchClient(URL url, ObjectMapper objectMapper) {
		super();
		this.url = url;
		this.httpClient = new DefaultHttpClient();
		this.objectMapper = objectMapper;
	}

	public SearchResult process(SearchOptions options, ProgressBar progressBar) {
		Request request = new Request(options);
		try {
			String queryAsJson = objectMapper.writeValueAsString(request);
			Log.d(getClass().getName(), "query as json : " + queryAsJson);
			
			String query = URLEncoder.encode(queryAsJson, ENCODING);
			HttpGet getRequest = new HttpGet(url.toString() + "?source=" + query);
			getRequest.addHeader(ACCEPT_ENCODING, GZIP);
			long now = System.currentTimeMillis();
			HttpResponse response = httpClient.execute(getRequest);
			String fileSize = getResponseSize(response);
			Log.i(getClass().getName(), "downloaded " + fileSize + " in " + (System.currentTimeMillis() - now) + "ms");
			progressBar.setProgress(33);
			InputStream content = getInputStream(response);
			return parse(content, progressBar);
		} catch (ClientProtocolException e) {
			Log.e(getClass().getName(), "process", e);
		} catch (IOException e) {
			Log.e(getClass().getName(), "process", e);
		}
		return null;
	}

	private String getResponseSize(HttpResponse response) {
		return DisplaySizeUtil.getFileSize(response.getEntity().getContentLength());
	}

	private InputStream getInputStream(HttpResponse response) throws IOException {
		InputStream content = response.getEntity().getContent();
		Header contentEncoding = response.getFirstHeader(CONTENT_ENCODING);
		if (contentEncoding != null && GZIP.equalsIgnoreCase(contentEncoding.getValue())) {
			return new GZIPInputStream(content);
		}
		return content;
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
		}
		
		Log.i(getClass().getName(), "parse took " + (System.currentTimeMillis() - now) + "ms");
		progressBar.setProgress(66);
		return searchResult;
	}
}
