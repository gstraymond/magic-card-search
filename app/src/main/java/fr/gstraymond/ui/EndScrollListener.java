package fr.gstraymond.ui;

import android.support.design.widget.FloatingActionButton;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.magic.card.search.commons.log.Log;

import fr.gstraymond.R;
import fr.gstraymond.android.CardListActivity;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class EndScrollListener implements OnScrollListener {

    private boolean canLoadMoreItems = true;
    private CardListActivity activity;
    private FloatingActionButton fab;

    private Log log = new Log(this);

    public EndScrollListener(CardListActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onScroll(AbsListView view,
                         int firstVisible,
                         int visibleCount,
                         int totalCount) {
        if (canLoadMoreItems && hasEndReached(firstVisible, visibleCount, totalCount)) {
            if (totalCount != activity.getTotalCardCount()) {
                log.i("onScroll - endReached");
                SearchOptions options = activity.getCurrentSearch().setAppend(true).setAddToHistory(false);
                showLoadingToast();
                new SearchProcessor(activity, options, R.string.loading_more).execute();
            } else {
                fab.hide();
            }
        } else {
            fab.show();
        }
    }

    private boolean hasEndReached(int firstVisible, int visibleCount, int totalCount) {
        return firstVisible + visibleCount >= totalCount;
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

    public void setCanLoadMoreItems(boolean canLoadMoreItems) {
        this.canLoadMoreItems = canLoadMoreItems;
    }

    public void setFab(FloatingActionButton fab) {
        this.fab = fab;
    }
}
