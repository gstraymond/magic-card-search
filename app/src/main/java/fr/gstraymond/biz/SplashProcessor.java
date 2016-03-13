package fr.gstraymond.biz;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.SplashScreen;
import fr.gstraymond.search.model.response.SearchResult;
import fr.gstraymond.tools.Log;

public class SplashProcessor extends AsyncTask<Void, Integer, SearchResult> {

    private SplashScreen activity;
    private ProgressBar progressBar;
    private SearchOptions options;
    private Log log = new Log(this);

    public SplashProcessor(SplashScreen activity, SearchOptions options) {
        super();
        this.activity = activity;
        this.progressBar = getProgressBar();

        progressBar.setProgress(0);
        this.options = options;
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = getCustomApplication().getElasticSearchClient().process(options, progressBar);

        if (searchResult != null && searchResult.getHits() != null) {
            log.i(searchResult.getHits().getTotal() + " cards found in " + searchResult.getTook() + " ms");
        }
        log.i("search took " + (System.currentTimeMillis() - now) + "ms");

        return searchResult;
    }

    private CustomApplication getCustomApplication() {
        return (CustomApplication) activity.getApplication();
    }

    @Override
    protected void onPostExecute(SearchResult result) {
        activity.startNextActivity(result);
    }

    private ProgressBar getProgressBar() {
        return (ProgressBar) activity.findViewById(R.id.progress_bar);
    }
}
