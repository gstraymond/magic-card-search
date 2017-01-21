package fr.gstraymond.android;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.magic.card.search.commons.json.MapperUtil;

import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SplashProcessor;
import fr.gstraymond.models.search.response.SearchResult;

public class SplashScreenActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SearchOptions options = new SearchOptions().updateRandom(true).updateAddToHistory(false);
        String action = getIntent().getAction();
        if (Intent.ACTION_SEARCH.equals(action)
                || "com.google.android.gms.actions.SEARCH_ACTION".equals(action)) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            options = options.updateQuery(query).updateRandom(false).updateAddToHistory(true);
        }
		/* lancement de la recherche */
        new SplashProcessor(this, options).execute();
    }

    public void startNextActivity(SearchResult result) {
        String resultAsString = MapperUtil.fromType(getObjectMapper(), SearchResult.class).asJsonString(result);

        Intent intent = new Intent(this, CardListActivity.class);
        intent.putExtra(CardListActivity.Companion.getCARD_RESULT(), resultAsString);
        startActivity(intent);

        finish();
    }
}
