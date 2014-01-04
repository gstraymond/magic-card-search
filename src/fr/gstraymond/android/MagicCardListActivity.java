package fr.gstraymond.android;

import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.ActivityUtil;
import fr.gstraymond.ui.EndScrollListener;
import fr.gstraymond.ui.TextListener;

public class MagicCardListActivity extends Activity implements
		MagicCardListFragment.Callbacks, SetListFragment.Callbacks {
	private static final String CURRENT_SEARCH = "currentSearch";

	public static final String MAGIC_CARD_RESULT = "result";

	private boolean twoPaneMode;

	private TextListener textListener;
	private EndScrollListener endScrollListener;
	private SearchView searchView;
	private Menu menu;

	private MagicCard currentCard;
	private SearchOptions currentSearch;
	private boolean isRestored = false;

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
			SearchOptions savedSearch = savedInstanceState.getParcelable(CURRENT_SEARCH);
			if (savedSearch != null) {
				currentSearch = savedSearch;
				isRestored = true;
				Log.d(getClass().getName(), "Restored search : " + currentSearch);
			}
		}

		if (findViewById(R.id.magiccard_detail_container) != null) {
			twoPaneMode = true;
			MagicCardListFragment listFragment = new MagicCardListFragment();
			getFragmentManager().beginTransaction().replace(R.id.magiccard_list, listFragment).commit();
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (twoPaneMode) {
			Fragment listFragment = getFragmentManager().findFragmentById(R.id.magiccard_list);
			((MagicCardListFragment) listFragment).setActivateOnItemClick(true);
		}

		if (currentSearch == null) {
			currentSearch = new SearchOptions().setQuery("*");
		}
		
		String resultAsString = getIntent().getStringExtra(MAGIC_CARD_RESULT);
		if (resultAsString != null && !isRestored) {
			new UIUpdater(this, resultAsString).execute();
		} else {
			new SearchProcessor(this, currentSearch, R.string.loading_initial).execute();	
		}
	}

	/**
	 * Callback method from {@link MagicCardListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Parcelable card) {
		currentCard = (MagicCard) card;
		if (twoPaneMode) {
			Bundle bundle = new Bundle();
			bundle.putParcelable(MAGIC_CARD, card);
			
			MagicCardDetailFragment detailFragment = new MagicCardDetailFragment();
			detailFragment.setArguments(bundle);
			getFragmentManager().beginTransaction()
				.replace(R.id.magiccard_detail_container, detailFragment)
				.commit();
			
			Fragment setListFragment = new SetListFragment();
			setListFragment.setArguments(bundle);
			getFragmentManager().beginTransaction()
				.replace(R.id.magiccard_set_list, setListFragment)
				.commit();

		} else {
			Intent intent = ActivityUtil.getIntent(this, MagicCardDetailActivity.class);
			intent.putExtra(MAGIC_CARD, card);
			startActivity(intent);
		}
	}

	@Override
	public void onItemSelected(int id) {
		Intent intent = ActivityUtil.getIntent(this, MagicCardPagerActivity.class);
		intent.putExtra(MAGIC_CARD, currentCard);
		intent.putExtra(POSITION, id);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;

		MenuInflater inflater = getMenuInflater();
		
		if (twoPaneMode) {
			inflater.inflate(R.menu.magiccard_twopane_menu, menu);
		} else {
			inflater.inflate(R.menu.magiccard_list_menu, menu);
		}

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

		case R.id.pictures_tab:
			Intent intent = ActivityUtil.getIntent(this, MagicCardPagerActivity.class);
			intent.putExtra(MAGIC_CARD, currentCard);
			startActivity(intent);
			return true;
			
		case R.id.clear_tab:
			resetSearchView();
			SearchOptions options = new SearchOptions().setQuery("*");
			new SearchProcessor(this, options, R.string.loading_clear).execute();
			return true;

		case R.id.help_tab:
			Intent helpIntent = ActivityUtil.getIntent(this, HelpActivity.class);
			startActivity(helpIntent);
			return true;
			
		}
		
		return super.onOptionsItemSelected(item);
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
		outState.putParcelable(CURRENT_SEARCH, currentSearch);
		Log.d(getClass().getName(), "onSaveInstanceState " + outState);
	}

	public View getCardView() {
		return findViewById(R.id.magiccard_list);
	}

	public View getFacetView() {
		return findViewById(R.id.facet_list);
	}

	public View getPicturesView() {
		return findViewById(R.id.pictures_layout);
	}

	public View getDetailView() {
		return findViewById(R.id.magiccard_detail);
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
