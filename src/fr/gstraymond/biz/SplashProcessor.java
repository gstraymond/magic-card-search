package fr.gstraymond.biz;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.SplashScreen;
import fr.gstraymond.magicsearch.model.response.SearchResult;

public class SplashProcessor extends AsyncTask<Void, Integer, SearchResult> {

	private SplashScreen activity;
	private ProgressBar progressBar;
	private SearchOptions options = new SearchOptions();

	public SplashProcessor(SplashScreen activity, SearchOptions options) {
		super();
		this.activity = activity;
		this.progressBar = getProgressBar();

		progressBar.setProgress(0);
		this.options = options;
	}

	@Override
	protected SearchResult doInBackground(Void... params) {
		CustomApplication applicationContext = (CustomApplication) activity.getApplicationContext();

		Log.i(getClass().getName(), "launchSearch: " + options);

		SearchResult searchResult = applicationContext.getElasticSearchClient().process(options, progressBar);
		if (searchResult != null) {
			Log.i(getClass().getName(), "Server search took " + searchResult.getTook() + " ms");
			if (searchResult.getHits() != null) {
				Log.i(getClass().getName(), "total cards " + searchResult.getHits().getTotal());
			}
		}
		return searchResult;
	}


	@Override
	protected void onPostExecute(SearchResult result) {
		super.onPostExecute(result);
		activity.startNextActivity(result);
	}

	private ProgressBar getProgressBar() {
		return (ProgressBar) activity.findViewById(R.id.progress_bar);
	}
}
