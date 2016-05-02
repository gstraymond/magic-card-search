package fr.gstraymond.android;

import com.magic.card.search.commons.application.BaseApplication;
import com.magic.card.search.commons.log.Log;

import java.net.MalformedURLException;
import java.net.URL;

import fr.gstraymond.R;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.ui.CastingCostAssetLoader;

public class CustomApplication extends BaseApplication {

    private static final String TABLET = "tablet";

    private static final String SEARCH_SERVER_HOST = "engine.magic-card-search.com:8080";
//	private static final String SEARCH_SERVER_HOST = "local-gsr:9200";

    private ElasticSearchClient elasticSearchClient;
    private CastingCostAssetLoader castingCostAssetLoader;
    private Boolean isTablet;
    private Log log = new Log(this);

    @Override
    public void onCreate() {
        super.onCreate();
        initElasticSearchClient();
        initIsTablet();
    }

    public void init() {
        initCastingCostAssetLoader();
    }

    private void initElasticSearchClient() {
        log.d("initElasticSearchClient");
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

    // FIXME find another way
    private void initIsTablet() {
        log.d("initIsTablet");
        String mode = getApplicationContext().getString(R.string.mode);
        setTablet(TABLET.equals(mode));
    }

    public ElasticSearchClient getElasticSearchClient() {
        return elasticSearchClient;
    }

    public CastingCostAssetLoader getCastingCostAssetLoader() {
        if (castingCostAssetLoader == null) {
            initCastingCostAssetLoader();
        }
        return castingCostAssetLoader;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }
}
