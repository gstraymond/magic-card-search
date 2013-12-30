package fr.gstraymond.ui;

import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import fr.gstraymond.R;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardListFragment;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

public class EndScrollListener implements OnScrollListener {

	private boolean canLoadMoreItems = true;
	private MagicCardListActivity activity;

	public EndScrollListener(MagicCardListActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount,
			int totalCount) {
		boolean endReached = totalCount > 10
				&& firstVisible + visibleCount >= totalCount;
		if (canLoadMoreItems && endReached) {

			FragmentManager fragmentManager = activity.getFragmentManager();
			MagicCardListFragment magicCardListFragment = (MagicCardListFragment) fragmentManager.findFragmentById(R.id.magiccard_list);
			int cardListCount = magicCardListFragment.getTotalCardCount();

			boolean allCardsLoaded = totalCount == cardListCount;
			if (!allCardsLoaded) {
				Log.i(this.getClass().getName(), "onScroll - endReached");
				SearchOptions options = activity.getCurrentSearch().setAppend(true);
				new SearchProcessor(activity, options, R.string.loading_more).execute();
			}
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int arg1) {
	}

	public FragmentActivity getActivity() {
		return activity;
	}

	public boolean isCanLoadMoreItems() {
		return canLoadMoreItems;
	}

	public void setCanLoadMoreItems(boolean canLoadMoreItems) {
		this.canLoadMoreItems = canLoadMoreItems;
	}

}
