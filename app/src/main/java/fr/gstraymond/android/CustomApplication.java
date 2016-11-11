package fr.gstraymond.android;

import com.magic.card.search.commons.application.BaseApplication;
import com.magic.card.search.commons.json.MapperUtil;
import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.biz.ElasticSearchClient;
import fr.gstraymond.db.json.Decklist;
import fr.gstraymond.db.json.JsonDeck;
import fr.gstraymond.db.json.JsonHistoryDataSource;
import fr.gstraymond.db.json.JsonList;
import fr.gstraymond.db.json.Wishlist;
import fr.gstraymond.impex.DeckResolver;
import fr.gstraymond.network.ElasticSearchConnector;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.tools.VersionUtils;

public class CustomApplication extends BaseApplication {

    private static final String TABLET = "tablet";

    private ElasticSearchClient elasticSearchClient;
    private JsonHistoryDataSource jsonHistoryDataSource;
    private Wishlist wishlist;
    private Decklist decklist;
    private JsonDeck jsonDeck;
    private DeckResolver deckResolver;

    private Boolean isTablet;

    @Override
    public void onCreate() {
        super.onCreate();
        ElasticSearchConnector<SearchResult> connector = new ElasticSearchConnector<>(VersionUtils.getAppName(this), MapperUtil.fromType(getObjectMapper(), SearchResult.class));
        initJsonHistoryDataSource();
        initElasticSearchClient(connector);
        initIsTablet();
        migrateHistory();
        wishlist = new Wishlist(this, getObjectMapper());
        decklist = new Decklist(this, getObjectMapper());
        jsonDeck = new JsonDeck(this, getObjectMapper());
        deckResolver = new DeckResolver(connector);
    }

    private void initElasticSearchClient(ElasticSearchConnector<SearchResult> connector) {
        this.elasticSearchClient = new ElasticSearchClient(connector, getJsonHistoryDataSource());
    }

    // FIXME find another way
    private void initIsTablet() {
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

    public Wishlist getWishlist() {
        return wishlist;
    }

    public JsonDeck getJsonDeck() {
        return jsonDeck;
    }

    public DeckResolver getDeckResolver() {
        return deckResolver;
    }

    public Decklist getDecklist() {
        return decklist;
    }
}
