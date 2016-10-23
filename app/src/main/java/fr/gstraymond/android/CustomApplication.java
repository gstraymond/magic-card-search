package fr.gstraymond.android;

import com.magic.card.search.commons.application.BaseApplication;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.db.json.JsonList;

public class CustomApplication extends BaseApplication {

    private static final String TABLET = "tablet";

    private ElasticSearchClient elasticSearchClient;
    private JsonHistoryDataSource jsonHistoryDataSource;
    private JsonList wishlist;
    private Boolean isTablet;
    private Log log = new Log(this);

    @Override
    public void onCreate() {
        super.onCreate();
        initJsonHistoryDataSource();
        initElasticSearchClient();
        initIsTablet();
        migrateHistory();
        wishlist = new JsonList(this, getObjectMapper(), "wishlist");
    }

    private void initElasticSearchClient() {
        log.d("initElasticSearchClient");
        this.elasticSearchClient = new ElasticSearchClient(getObjectMapper(), this, getJsonHistoryDataSource());
    }

    // FIXME find another way
    private void initIsTablet() {
        log.d("initIsTablet");
        String mode = getApplicationContext().getString(R.string.mode);
        setTablet(TABLET.equals(mode));
    }

    private void initJsonHistoryDataSource() {
        this.jsonHistoryDataSource = new JsonHistoryDataSource(this, getObjectMapper());
    }

    public ElasticSearchClient getElasticSearchClient() {
        return elasticSearchClient;
    }

    public boolean isTablet() {
        return isTablet;
    }

    public void setTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    public JsonHistoryDataSource getJsonHistoryDataSource() {
        if (jsonHistoryDataSource == null) {
            initJsonHistoryDataSource();
        }
        return jsonHistoryDataSource;
    }

    private void migrateHistory() {
        jsonHistoryDataSource.migrate();
    }

    public JsonList getWishlist() {
        return wishlist;
    }
}
