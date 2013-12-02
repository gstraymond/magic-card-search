package fr.gstraymond.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.R;
import fr.gstraymond.biz.ApplicationLoader;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SplashProcessor;
import fr.gstraymond.magicsearch.model.response.SearchResult;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		/* chargement du client HTTP / object mapper / assets */
		new ApplicationLoader(this).execute();
		
		/* lancement de la recherche */
		SearchOptions options = new SearchOptions().setQuery("*");
		new SplashProcessor(this, options).execute();
	}

	public void startNextActivity(SearchResult result) {
		String resultAsString = getResultAsString(result);
		
		Intent intent = new Intent(this, MagicCardListActivity.class);
		intent.putExtra(MagicCardListActivity.MAGIC_CARD_RESULT, resultAsString);
		startActivity(intent);

		finish();
	}

	private String getResultAsString(SearchResult result) {
		try {
			return getObjectMapper().writeValueAsString(result);
		} catch (JsonProcessingException e) {
			Log.e(getClass().getName(), "getResultAsString", e);
		}
		return null;
	}

	public CustomApplication getCustomApplication() {
		return (CustomApplication) getApplicationContext();
	}
	
	private ObjectMapper getObjectMapper() {
		return getCustomApplication().getObjectMapper();
	}
}
