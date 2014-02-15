package fr.gstraymond.android;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Application;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.gstraymond.R;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class CustomApplication extends Application {

	private static final String TABLET = "tablet";
	
	private static final String SEARCH_SERVER_HOST = "engine.magic-card-search.com:8080";
//	private static final String SEARCH_SERVER_HOST = "local-gsr:9200";
	
	private ElasticSearchClient elasticSearchClient;
	private CastingCostAssetLoader castingCostAssetLoader;
	private ObjectMapper objectMapper;
	private Boolean isTablet;
	
	public void init() {
		initObjectMapper();
		initElasticSearchClient();
		initCastingCostAssetLoader();
		initIsTablet();
	}

	private void initElasticSearchClient() {
		try {
			URL url = new URL("http://" + SEARCH_SERVER_HOST + "/magic/card/_search");
			this.elasticSearchClient = new ElasticSearchClient(url, getObjectMapper(), this);
		} catch (MalformedURLException e) {
			Log.e(getClass().getName(), "Error in constructor", e);
		}
	}
	
	private void initCastingCostAssetLoader() {
		CastingCostAssetLoader loader = new CastingCostAssetLoader();
		loader.init(this);
		this.castingCostAssetLoader = loader;
	}

	private void initObjectMapper() {
		this.objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
			.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	private void initIsTablet() {
		String mode =  getApplicationContext().getString(R.string.mode);
		setTablet(TABLET.equals(mode));
	}

	public ElasticSearchClient getElasticSearchClient() {
		if (elasticSearchClient == null) {
			initElasticSearchClient();
		}
		return elasticSearchClient;
	}

	public void setElasticSearchClient(ElasticSearchClient elasticSearchClient) {
		this.elasticSearchClient = elasticSearchClient;
	}

	public CastingCostAssetLoader getCastingCostAssetLoader() {
		if (castingCostAssetLoader == null) {
			initCastingCostAssetLoader();
		}
		return castingCostAssetLoader;
	}

	public void setCastingCostAssetLoader(CastingCostAssetLoader castingCostAssetLoader) {
		this.castingCostAssetLoader = castingCostAssetLoader;
	}

	public ObjectMapper getObjectMapper() {
		if (objectMapper == null) {
			initObjectMapper();
		}
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public boolean isTablet() {
		if (isTablet == null) {
			initIsTablet();
		}
		return isTablet;
	}

	public void setTablet(boolean isTablet) {
		this.isTablet = isTablet;
	}
}
