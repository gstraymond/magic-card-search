package fr.gstraymond.android;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import com.magic.card.search.commons.json.MapperUtil;
import fr.gstraymond.R;
import fr.gstraymond.biz.ApplicationLoader;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SplashProcessor;
import fr.gstraymond.search.model.response.SearchResult;

public class SplashScreenActivity extends CustomActivity {

    private MapperUtil<Object> mapperUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mapperUtil = MapperUtil.fromType(getObjectMapper(), Object.class);

		/* chargement du client HTTP / object mapper / assets */
        new ApplicationLoader(this).execute();

        SearchOptions options = new SearchOptions().setRandom(true).setAddToHistory(false);
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            options = options.setQuery(query).setRandom(false).setAddToHistory(true);
        }
		/* lancement de la recherche */
        new SplashProcessor(this, options).execute();
    }

    public void startNextActivity(SearchResult result) {
        String resultAsString = mapperUtil.asJsonString(result);

        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra(CardListActivity.CARD_RESULT, resultAsString);
        startActivity(intent);

        finish();
    }
}
