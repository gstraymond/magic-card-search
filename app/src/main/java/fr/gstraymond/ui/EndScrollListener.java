package fr.gstraymond.ui;

import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class EndScrollListener implements OnScrollListener {

    private boolean canLoadMoreItems = true;
    private CardListActivity activity;

    public EndScrollListener(CardListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount,
                         int totalCount) {
        boolean endReached = totalCount > 10
                && firstVisible + visibleCount >= totalCount;
        if (canLoadMoreItems && endReached) {
            int cardListCount = activity.getTotalCardCount();

            boolean allCardsLoaded = totalCount == cardListCount;
            if (!allCardsLoaded) {
                Log.i(this.getClass().getName(), "onScroll - endReached");
                SearchOptions options = activity.getCurrentSearch().setAppend(true).setAddToHistory(false);
                showLoadingToast();
                new SearchProcessor(activity, options, R.string.loading_more).execute();
            }
        }
    }

    private void showLoadingToast() {
        if (activity.isTablet()) {
            return;
        }
        Toast loadingToast = makeText(activity, R.string.loading_more, LENGTH_SHORT);
        activity.setLoadingToast(loadingToast);
        loadingToast.show();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int arg1) {
    }

    public boolean isCanLoadMoreItems() {
        return canLoadMoreItems;
    }

    public void setCanLoadMoreItems(boolean canLoadMoreItems) {
        this.canLoadMoreItems = canLoadMoreItems;
    }

}