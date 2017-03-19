package fr.gstraymond.biz;

import android.os.AsyncTask;

import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.SplashScreenActivity;
import fr.gstraymond.models.search.response.SearchResult;

public class SplashProcessor extends AsyncTask<Void, Integer, SearchResult> {

    private SplashScreenActivity activity;
    private SearchOptions options;
    private ElasticSearchClient elasticSearchClient;

    public SplashProcessor(SplashScreenActivity activity, SearchOptions options) {
        super();
        this.activity = activity;
        this.elasticSearchClient = getCustomApplication().getElasticSearchClient();
        this.options = options;
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        return elasticSearchClient.process(options);
    }

    private CustomApplication getCustomApplication() {
        return (CustomApplication) activity.getApplication();
    }

    @Override
    protected void onPostExecute(SearchResult result) {
        activity.startNextActivity(result);
    }
}
