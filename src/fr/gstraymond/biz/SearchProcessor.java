package fr.gstraymond.biz;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardListFragment;
import fr.gstraymond.magicsearch.model.response.SearchResult;

public class SearchProcessor extends AsyncTask<Void, Void, Boolean> {

	private MagicCardListActivity activity;
	private ProgressBar progressBar;
	private TextView welcomeTextView;
	
	private SearchOptions options = new SearchOptions();
	 
	private SearchResult searchResult;

	public SearchProcessor(MagicCardListActivity activity, SearchOptions options, int loadingText) {
		super();
		this.activity = activity;
		this.progressBar = getProgressBar();
		this.welcomeTextView = getWelcomeTextView();
		this.options = options;

		disableSearch();
		storeCurrentSearch(options);

		welcomeTextView.setText(activity.getString(loadingText));
		progressBar.setProgress(0);
	}

	private void storeCurrentSearch(SearchOptions options) {
		activity.setCurrentSearch(options);
	}

	private ProgressBar getProgressBar() {
		return (ProgressBar) activity.findViewById(R.id.progress_bar);
	} 

	private TextView getWelcomeTextView() {
		return (TextView) activity.findViewById(R.id.welcome_text_view);
	}
	
	@Override
	protected Boolean doInBackground(Void... params) {
		long now = System.currentTimeMillis();
		searchResult = launchSearch(options);
		Log.i(getClass().getName(), "search took " + (System.currentTimeMillis() - now) + "ms");
		return true;
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
	protected void onPostExecute(Boolean result) {
		long now = System.currentTimeMillis();
		progressBar.setProgress(100);

		new UIUpdater(activity).onPostExecute(searchResult);
		enableSearch();
		// suppression du focus sur le search et fermeture du clavier
		getActivity().getSearchView().clearFocus();
		
		Log.i(getClass().getName(), "ui update took " + (System.currentTimeMillis() - now) + "ms");
	}

	private FragmentManager getFragmentManager() {
		return getActivity().getSupportFragmentManager();
	}

	private MagicCardListFragment getMagicCardListFragment() {
		return (MagicCardListFragment) getFragmentManager().findFragmentById(R.id.magiccard_list);
	}

	private SearchResult launchSearch(SearchOptions options) {
		if (options.isAppend()) {
			options.setFrom(getMagicCardListFragment().getCardListCount());
		}

		CustomApplication applicationContext = (CustomApplication) getActivity().getApplicationContext();
		
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

	public MagicCardListActivity getActivity() {
		return activity;
	}

	public void setActivity(MagicCardListActivity activity) {
		this.activity = activity;
	}
}
