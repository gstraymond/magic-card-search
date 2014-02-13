package fr.gstraymond.biz;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardListFragment;
import fr.gstraymond.magicsearch.model.response.SearchResult;

public class SearchProcessor extends AsyncTask<Void, Void, SearchResult> {

	private MagicCardListActivity activity;
	private ProgressBar progressBar;
	private TextView welcomeTextView;
	
	private SearchOptions options = new SearchOptions();

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
	protected SearchResult doInBackground(Void... params) {
		long now = System.currentTimeMillis();
		SearchResult searchResult = launchSearch(options);
		Log.i(getClass().getName(), "search took " + (System.currentTimeMillis() - now) + "ms");
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
		progressBar.setProgress(100);
		
		if (options.isAppend()) {
			if (activity.getLoadingToast() != null) {
				activity.getLoadingToast().cancel();
			}
			makeText(activity, R.string.toasting_loading_finished, LENGTH_SHORT).show();
		}

		new UIUpdater(activity).onPostExecute(searchResult);
		enableSearch();
		// suppression du focus sur le search et fermeture du clavier
		getActivity().getSearchView().clearFocus();
	}

	private FragmentManager getFragmentManager() {
		return getActivity().getFragmentManager();
	}

	private MagicCardListFragment getMagicCardListFragment() {
		return (MagicCardListFragment) getFragmentManager().findFragmentById(R.id.magiccard_list);
	}

	private SearchResult launchSearch(SearchOptions options) {
		if (options.isAppend()) {
			options.setFrom(getMagicCardListFragment().getCardListCount());
		}
		
		SearchResult searchResult = getApplicationContext().getElasticSearchClient().process(options, progressBar);
		
		if (searchResult != null && searchResult.getHits() != null) {
			Log.i(getClass().getName(), searchResult.getHits().getTotal() + " cards found in " + searchResult.getTook() + " ms");
		}
		
		return searchResult;
	}

	private CustomApplication getApplicationContext() {
		return (CustomApplication) getActivity().getApplicationContext();
	}

	public MagicCardListActivity getActivity() {
		return activity;
	}

	public void setActivity(MagicCardListActivity activity) {
		this.activity = activity;
	}
}
