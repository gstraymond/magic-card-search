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

	@Override
	public void onCreate() {
		super.onCreate();

		this.elasticSearchClient = initElasticSearchClient();
		this.castingCostAssetLoader = new CastingCostAssetLoader();
		castingCostAssetLoader.init(this);
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

	public ElasticSearchClient getElasticSearchClient() {
		return elasticSearchClient;
	}

	public void setElasticSearchClient(ElasticSearchClient elasticSearchClient) {
		this.elasticSearchClient = elasticSearchClient;
	}


	public CastingCostAssetLoader getCastingCostAssetLoader() {
		return castingCostAssetLoader;
	}

	public void setCastingCostAssetLoader(CastingCostAssetLoader castingCostAssetLoader) {
		this.castingCostAssetLoader = castingCostAssetLoader;
	}
}
