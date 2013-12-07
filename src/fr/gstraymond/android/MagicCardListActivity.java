package fr.gstraymond.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;
import fr.gstraymond.biz.UIUpdater;
import fr.gstraymond.ui.EndScrollListener;
import fr.gstraymond.ui.TextListener;

public class MagicCardListActivity extends FragmentActivity implements
		MagicCardListFragment.Callbacks {
	public static final String MAGIC_CARD_RESULT = "result";

	private boolean mTwoPane;

	private TextListener textListener;
	private EndScrollListener endScrollListener;
	private SearchView searchView;
	private Menu menu;

	private SearchOptions currentSearch;

	public MagicCardListActivity() {
		super();

		this.textListener = new TextListener(this);
		this.endScrollListener = new EndScrollListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_magiccard_list);
		
		if (savedInstanceState != null) {
			Parcelable savedSearch = savedInstanceState.getParcelable("currentSearch");
			if (savedSearch != null) {
				currentSearch = savedInstanceState.getParcelable("currentSearch");
				Log.d(getClass().getName(), "Restored search : " + currentSearch);
			}
		}

		if (findViewById(R.id.magiccard_detail_container) != null) {
			mTwoPane = true;
			((MagicCardListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.magiccard_list))
					.setActivateOnItemClick(true);
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		if (currentSearch == null) {
			currentSearch = new SearchOptions().setQuery("*");
		}
		
		String resultAsString = getIntent().getExtras().getString(MAGIC_CARD_RESULT);
		if (resultAsString != null) {
			new UIUpdater(this, resultAsString).execute();
		} else {
			new SearchProcessor(this, currentSearch).execute();	
		}
	}

	/**
	 * Callback method from {@link MagicCardListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Parcelable item) {
		if (mTwoPane) {
			Bundle arguments = new Bundle();
			arguments.putParcelable(MagicCardDetailFragment.MAGIC_CARD, item);
			MagicCardDetailFragment fragment = new MagicCardDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.magiccard_detail_container, fragment)
					.commit();

		} else {
			Intent detailIntent = new Intent(this, MagicCardDetailActivity.class);
			detailIntent.putExtra(MagicCardDetailFragment.MAGIC_CARD, item);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.magiccard_list_menu, menu);

		searchView = new SearchView(this);
		searchView.setOnQueryTextListener(textListener);
		searchView.setQueryHint("black lotus, draw, sacrifice...");
		menu.findItem(R.id.search_tab).setActionView(searchView);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.list_tab:
			hide(getFacetView());
			show(getCardView());
			item.setVisible(false);
			menu.findItem(R.id.facet_tab).setVisible(true);
			return true;

		case R.id.facet_tab:
			hide(getCardView());
			show(getFacetView());
			item.setVisible(false);
			menu.findItem(R.id.list_tab).setVisible(true);
			return true;

		case R.id.clear_tab:
			resetSearchView();
			SearchOptions options = new SearchOptions().setQuery("*");
			new SearchProcessor(this, options).execute();
			return true;

		case R.id.help_en_tab:
			startHelpActivity(HelpActivity.EN);
			return true;

		case R.id.help_fr_tab:
			startHelpActivity(HelpActivity.FR);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void startHelpActivity(String language) {
		Intent intent = new Intent(this, HelpActivity.class);
		intent.putExtra(HelpActivity.LANGUAGE, language);
		startActivity(intent);
	}
	
	private void resetSearchView() {
		// buggy
        MenuItem menuItem = menu.findItem(R.id.search_tab);
		menuItem.collapseActionView();
		searchView.setIconified(true); 
        menuItem.collapseActionView();
		searchView.setIconified(true);
        searchView.setQuery("", false);
	}
	
	private void hide(View view) {
		view.setVisibility(View.GONE);
	}
	
	private void show(View view) {
		view.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("currentSearch", currentSearch);
	}

	public View getCardView() {
		return findViewById(R.id.magiccard_list);
	}

	public View getFacetView() {
		return findViewById(R.id.facet_list);
	}

	private CustomApplication getCustomApplication() {
		return (CustomApplication) getApplicationContext();
	}
	
	public ObjectMapper getObjectMapper() {
		return getCustomApplication().getObjectMapper();
	}

	public TextListener getTextListener() {
		return textListener;
	}

	public void setTextListener(TextListener textListener) {
		this.textListener = textListener;
	}

	public EndScrollListener getEndScrollListener() {
		return endScrollListener;
	}

	public void setEndScrollListener(EndScrollListener endScrollListener) {
		this.endScrollListener = endScrollListener;
	}

	public SearchOptions getCurrentSearch() {
		return currentSearch;
	}

	public void setCurrentSearch(SearchOptions currentSearch) {
		this.currentSearch = currentSearch;
	}

	public SearchView getSearchView() {
		return searchView;
	}
}
