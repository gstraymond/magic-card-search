package fr.gstraymond.android;

import android.content.Intent;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import fr.gstraymond.R;
import fr.gstraymond.biz.ApplicationLoader;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SplashProcessor;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.tools.MapperUtil;
import io.fabric.sdk.android.Fabric;

public class SplashScreen extends CustomActivity {

    private MapperUtil<Object> mapperUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);
        setContentView(R.layout.activity_splash);

        mapperUtil = new MapperUtil<>(getObjectMapper(), Object.class);

		/* chargement du client HTTP / object mapper / assets */
        new ApplicationLoader(this).execute();
		
		/* lancement de la recherche */
        SearchOptions options = new SearchOptions().setRandom(true).setAddToHistory(false);
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
