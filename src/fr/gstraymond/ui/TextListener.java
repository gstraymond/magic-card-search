package fr.gstraymond.ui;


import java.util.List;
import java.util.Map;

import android.widget.SearchView.OnQueryTextListener;
import fr.gstraymond.R;
import fr.gstraymond.android.MagicCardListActivity;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;

public class TextListener implements OnQueryTextListener {

	private MagicCardListActivity activity;
	private boolean canSearch = true;
	
	public TextListener(MagicCardListActivity activity) {
		this.activity = activity;
	}

	@Override
	public boolean onQueryTextChange(String text) {
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String text) {
		if (canSearch) {
			Map<String, List<String>> facets = activity.getCurrentSearch().getFacets();
			SearchOptions options = new SearchOptions().setQuery(text).setFacets(facets);
			new SearchProcessor(activity, options, R.string.loading_initial).execute();
		}
		return true;
	}

	public void setCanSearch(boolean canSearch) {
		this.canSearch = canSearch;
	}

}
