package fr.gstraymond.biz;

import android.os.AsyncTask;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.models.search.response.SearchResult;

public class SearchProcessor extends AsyncTask<Void, Void, SearchResult> {

    private CardListActivity activity;

    private SearchOptions options;
    private Log log = new Log(this);

    public SearchProcessor(CardListActivity activity, SearchOptions options) {
        super();
        this.activity = activity;
        this.options = options;

        disableSearch();
        storeCurrentSearch(options);
    }

    private void storeCurrentSearch(SearchOptions options) {
        activity.setCurrentSearch(options);
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = launchSearch(options);
        log.i("search took " + (System.currentTimeMillis() - now) + "ms");
        return searchResult;
    }

    private void switchSearch(boolean _switch) {
        getActivity().getTextListener().setCanSearch(_switch);
        getActivity().getEndScrollListener().setCanLoadMoreItems(_switch);
    }

    private void disableSearch() {
        switchSearch(false);
    }

    private void enableSearch() {
        switchSearch(true);
    }

    @Override
    protected void onPostExecute(SearchResult searchResult) {
        new UIUpdater(activity).onPostExecute(searchResult);
        enableSearch();
    }

    private SearchResult launchSearch(SearchOptions options) {
        if (options.getAppend()) {
            options.setFrom(activity.getAdapter().getItemCount());
        }

        SearchResult searchResult = getApplicationContext().getElasticSearchClient().process(options);

        if (searchResult != null) {
            log.i(searchResult.getHits().getTotal() + " cards found in " + searchResult.getTook() + " ms");
        }

        return searchResult;
    }

    private CustomApplication getApplicationContext() {
        return (CustomApplication) getActivity().getApplication();
    }

    public CardListActivity getActivity() {
        return activity;
    }

    public void setActivity(CardListActivity activity) {
        this.activity = activity;
    }
}
