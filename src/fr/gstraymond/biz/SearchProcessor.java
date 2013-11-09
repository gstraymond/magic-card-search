package fr.gstraymond.biz;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.android.CustomApplication;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.android.MagicCardListFragment;
import fr.gstraymond.magicsearch.model.response.Hit;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.magicsearch.model.response.SearchResult;
import fr.gstraymond.magicsearch.model.response.facet.Term;
import fr.gstraymond.ui.FacetListAdapter;

public class SearchProcessor extends AsyncTask<String, Void, Boolean> {

	private MagicCardListActivity activity;
	private ProgressBar progressBar;
	private TextView welcomeTextView;
	private SearchOptions options = new SearchOptions();
	 
	private SearchResult searchResult;

	public SearchProcessor(MagicCardListActivity activity, SearchOptions options) {
		super();
		this.activity = activity;
		this.progressBar = getProgressBar();
		this.welcomeTextView = getWelcomeTextView();

		disableSearch();
		storeCurrentSearch(options);
		
		progressBar.setProgress(0);
		progressBar.setVisibility(View.VISIBLE);
		welcomeTextView.setVisibility(View.GONE);
		this.options = options;
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
	protected Boolean doInBackground(String... params) {
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
		progressBar.setProgress(33);

		updateUI();
		enableSearch();
		closeKeyboard();
		
		progressBar.setVisibility(View.GONE);
		welcomeTextView.setVisibility(View.VISIBLE);
		Log.i(getClass().getName(), "ui update took " + (System.currentTimeMillis() - now) + "ms");
	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		View currentFocus = activity.getCurrentFocus();
		if (currentFocus != null) {
			inputManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	private void updateUI() {
		int totalCardCount = 0;
		ArrayList<MagicCard> cards = new ArrayList<MagicCard>();

		if (searchResult.getHits() != null) {
			totalCardCount = searchResult.getHits().getTotal();
			for (Hit hit : searchResult.getHits().getHits()) {
				cards.add(hit.get_source());
			}
		}
		welcomeTextView.setText(totalCardCount + " card(s) found" + (options.isRandom() ? " at random" : ""));
		
		updateUIList(totalCardCount, cards);
		updateUIFacets();
	}

	private void updateUIList(int totalCardCount, ArrayList<MagicCard> cards) {
		if (options.isAppend()) {
			MagicCardListFragment fragment = getMagicCardListFragment();
			fragment.appendCards(cards);
		} else {
			Bundle bundle = new Bundle();
			bundle.putParcelableArrayList(MagicCardListFragment.CARDS, cards);
			bundle.putInt(MagicCardListFragment.TOTAL_CARD_COUNT, totalCardCount);
			Fragment fragment = new MagicCardListFragment();
			fragment.setArguments(bundle);
			getFragmentManager().beginTransaction().replace(R.id.magiccard_list, fragment).commit();
		}
	}

	private void updateUIFacets() {
		if (! options.isAppend()) {
			final FacetListAdapter facetListAdapter = new FacetListAdapter(searchResult.getFacets(), options);
			getFacetListView().setAdapter(facetListAdapter);
			getFacetListView().setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					Term term = facetListAdapter.getTerm(position);
					if (term.getCount() > -1) {
						String facet = facetListAdapter.getFacet(term);

						if (facetListAdapter.isTermSelected(term)) {
							options.removeFacet(facet, term.getTerm());
						} else {
							options.addFacet(facet, term.getTerm());
						}
						options.setAppend(false);
						options.setFrom(0);
						new SearchProcessor(activity, options).execute();
					}
				}
			});
		}
	}

	private FragmentManager getFragmentManager() {
		return getActivity().getSupportFragmentManager();
	}

	private MagicCardListFragment getMagicCardListFragment() {
		return (MagicCardListFragment) getFragmentManager().findFragmentById(R.id.magiccard_list);
	}

	private ListView getFacetListView() {
		return (ListView) getActivity().findViewById(R.id.facet_list);
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
