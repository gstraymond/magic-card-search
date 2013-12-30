package fr.gstraymond.ui;


import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
		Log.d(getClass().getName(), "text: " + text);
		
		if (text != null && text.length() > 0 && canSearch) {
			SearchOptions options = new SearchOptions().setQuery(text);
			new SearchProcessor(activity, options, R.string.loading_initial).execute();
		}
		return true;
	}

	public void setCanSearch(boolean canSearch) {
		this.canSearch = canSearch;
	}

	public FragmentActivity getActivity() {
		return activity;
	}

}
