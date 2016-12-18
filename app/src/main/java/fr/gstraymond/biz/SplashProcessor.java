package fr.gstraymond.biz;

import android.os.AsyncTask;
import android.widget.ProgressBar;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.SplashScreenActivity;
import fr.gstraymond.models.search.response.SearchResult;

public class SplashProcessor extends AsyncTask<Void, Integer, SearchResult> {

    private SplashScreenActivity activity;
    private SearchOptions options;
    private ProgressBarUpdater progressBarUpdater;
    private ElasticSearchClient elasticSearchClient;
    private Log log = new Log(this);

    public SplashProcessor(SplashScreenActivity activity, SearchOptions options) {
        super();
        this.activity = activity;
        this.progressBarUpdater = new ProgressBarUpdater(getProgressBar());
        this.elasticSearchClient = getCustomApplication().getElasticSearchClient();
        this.options = options;
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = elasticSearchClient.process(options, progressBarUpdater);

        if (searchResult != null) {
            log.i("%s cards found in %s ms", searchResult.getHits().getTotal(), searchResult.getTook());
        }
        log.i("search took %sms", System.currentTimeMillis() - now);

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
