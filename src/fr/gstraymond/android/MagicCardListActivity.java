package fr.gstraymond.android;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;
import static fr.gstraymond.constants.Consts.MAGIC_CARD;
import static fr.gstraymond.constants.Consts.POSITION;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;
import fr.gstraymond.biz.UIUpdater;
import fr.gstraymond.magicsearch.model.response.MagicCard;
import fr.gstraymond.tools.ActivityUtil;
import fr.gstraymond.tools.VersionUtils;
import fr.gstraymond.ui.EndScrollListener;
import fr.gstraymond.ui.TextListener;

public class MagicCardListActivity extends CustomActivity implements
		MagicCardListFragment.Callbacks, MagicCardDetailFragment.Callbacks {
	private static final String CURRENT_SEARCH = "currentSearch";

	public static final String MAGIC_CARD_RESULT = "result";

	private boolean twoPaneMode;

	private TextListener textListener;
	private EndScrollListener endScrollListener;
	private SearchView searchView;
	private Menu menu;

	private MagicCard currentCard;
	private int totalCardCount;
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
		}

		if (findViewById(R.id.search_input) != null) {
			searchView = (SearchView) findViewById(R.id.search_input);
			searchView.setOnQueryTextListener(textListener);
		}
		
		getActionBar().setHomeButtonEnabled(true);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (currentSearch == null) {
			currentSearch = new SearchOptions();
		}
		
		String resultAsString = getIntent().getStringExtra(MAGIC_CARD_RESULT);
		if (resultAsString != null && !isRestored) {
			new UIUpdater(this, resultAsString, getObjectMapper()).execute();
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
			
			TextView titleTextView = (TextView) findViewById(R.id.magiccard_detail_title);
			titleTextView.setText(MagicCardDetailActivity.formatTitle(this, currentCard));
			
			MagicCardDetailFragment detailFragment = new MagicCardDetailFragment();
			detailFragment.setArguments(bundle);
			getFragmentManager().beginTransaction()
				.replace(R.id.magiccard_detail_container, detailFragment)
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
		// first element is a card
		intent.putExtra(POSITION, id - 1);
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

		if (menu.findItem(R.id.search_tab) != null) {
			searchView = new SearchView(this);
			searchView.setIconifiedByDefault(false);
			searchView.setOnQueryTextListener(textListener);
			searchView.setQueryHint(getString(R.string.search_hint));
			menu.findItem(R.id.search_tab).setActionView(searchView);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case android.R.id.home:
			String version = "Version " + VersionUtils.getAppVersion(this);
			makeText(this, version, LENGTH_SHORT).show();
			return true;

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
			SearchOptions options = new SearchOptions();
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
        if (menuItem != null) {
        	menuItem.collapseActionView();
        }
		searchView.setIconified(true); 
        if (menuItem != null) {
        	menuItem.collapseActionView();
        }
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

	public int getTotalCardCount() {
		return totalCardCount;
	}

	public void setTotalCardCount(int totalCardCount) {
		this.totalCardCount = totalCardCount;
	}
}
