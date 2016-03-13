package fr.gstraymond.android;

import android.app.Application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.MalformedURLException;
import java.net.URL;

import fr.gstraymond.R;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.cache.BitmapCache;
import fr.gstraymond.tools.Log;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class CustomApplication extends Application {

    private static final String TABLET = "tablet";

    private static final String SEARCH_SERVER_HOST = "engine.magic-card-search.com:8080";
//	private static final String SEARCH_SERVER_HOST = "local-gsr:9200";

    private ElasticSearchClient elasticSearchClient;
    private CastingCostAssetLoader castingCostAssetLoader;
    private ObjectMapper objectMapper;
    private Boolean isTablet;
    private BitmapCache bitmapCache;
    private Log log = new Log(this);

    public void init() {
        initObjectMapper();
        initElasticSearchClient();
        initCastingCostAssetLoader();
        initIsTablet();
        initBitmapCache();
    }

    private void initElasticSearchClient() {
        try {
            URL url = new URL("http://" + SEARCH_SERVER_HOST + "/magic/card/_search");
            this.elasticSearchClient = new ElasticSearchClient(url, getObjectMapper(), this);
        } catch (MalformedURLException e) {
            log.e("Error in constructor", e);
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
        String mode = getApplicationContext().getString(R.string.mode);
        setTablet(TABLET.equals(mode));
    }

    private void initBitmapCache() {
        setBitmapCache(new BitmapCache());
    }

    public ElasticSearchClient getElasticSearchClient() {
        if (elasticSearchClient == null) {
            initElasticSearchClient();
        }
        return elasticSearchClient;
    }

    public CastingCostAssetLoader getCastingCostAssetLoader() {
        if (castingCostAssetLoader == null) {
            initCastingCostAssetLoader();
        }
        return castingCostAssetLoader;
    }

    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            initObjectMapper();
        }
        return objectMapper;
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

    public BitmapCache getBitmapCache() {
        if (bitmapCache == null) {
            initBitmapCache();
        }
        return bitmapCache;
    }

    public void setBitmapCache(BitmapCache bitmapCache) {
        this.bitmapCache = bitmapCache;
    }
}
