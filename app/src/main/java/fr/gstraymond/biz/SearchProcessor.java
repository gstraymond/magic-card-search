package fr.gstraymond.biz;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.widget.TextView;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.fragment.CardListFragment;
import fr.gstraymond.models.search.response.SearchResult;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class SearchProcessor extends AsyncTask<Void, Void, SearchResult> {

    private CardListActivity activity;

    private SearchOptions options;
    private Log log = new Log(this);

    public SearchProcessor(CardListActivity activity, SearchOptions options, int loadingText) {
        super();
        this.activity = activity;
        this.options = options;

        disableSearch();
        storeCurrentSearch(options);

        TextView welcomeTextView = getWelcomeTextView();
        welcomeTextView.setText(activity.getString(loadingText));
    }

    private void storeCurrentSearch(SearchOptions options) {
        activity.setCurrentSearch(options);
    }

    private TextView getWelcomeTextView() {
        return (TextView) activity.findViewById(R.id.welcome_text_view);
    }

    @Override
    protected SearchResult doInBackground(Void... params) {
        long now = System.currentTimeMillis();
        SearchResult searchResult = launchSearch(options);
        log.i("search took " + (System.currentTimeMillis() - now) + "ms");
        return searchResult;
    }

    private void switchSearch(boolean _switch) {
        if (getActivity().getTextListener() != null) {
            getActivity().getTextListener().setCanSearch(_switch);
        }

        if (getActivity().getEndScrollListener() != null) {
            getActivity().getEndScrollListener().setCanLoadMoreItems(_switch);
        }
    }

    private void disableSearch() {
        switchSearch(false);
    }

    private void enableSearch() {
        switchSearch(true);
    }

    @Override
    protected void onPostExecute(SearchResult searchResult) {

        if (options.getAppend()) {
            if (activity.getLoadingToast() != null) {
                activity.getLoadingToast().cancel();
            }
            makeText(activity, R.string.toasting_loading_finished, LENGTH_SHORT).show();
        }

        new UIUpdater(activity).onPostExecute(searchResult);
        enableSearch();
    }

    private FragmentManager getFragmentManager() {
        return getActivity().getFragmentManager();
    }

    private CardListFragment getCardListFragment() {
        return (CardListFragment) getFragmentManager().findFragmentById(R.id.card_list);
    }

    private SearchResult launchSearch(SearchOptions options) {
        if (options.getAppend()) {
            options.setFrom(getCardListFragment().getCardListCount());
        }

        SearchResult searchResult = getApplicationContext().getElasticSearchClient().process(options, activity.getProgressBarUpdater());

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
