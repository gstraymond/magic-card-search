package fr.gstraymond.android;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Application;
import android.util.Log;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class CustomApplication extends Application {

	private static final String SEARCH_SERVER_HOST = "engine.magic-card-search.com:8080";
//	private static final String SEARCH_SERVER_HOST = "local-gsr:9200";
	
	private ElasticSearchClient elasticSearchClient;
	private CastingCostAssetLoader castingCostAssetLoader;
	
	public void init() {
		this.elasticSearchClient = initElasticSearchClient();
		this.castingCostAssetLoader = initCastingCostAssetLoader();
	}

	private ElasticSearchClient initElasticSearchClient() {
		try {
			URL url = new URL("http://" + SEARCH_SERVER_HOST + "/magic/card/_search");
			return new ElasticSearchClient(url);
		} catch (MalformedURLException e) {
			Log.e(getClass().getName(), "Error in constructor", e);
		}
		return null;
	}
	
	private CastingCostAssetLoader initCastingCostAssetLoader() {
		CastingCostAssetLoader loader = new CastingCostAssetLoader();
		loader.init(this);
		return loader;
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
}
