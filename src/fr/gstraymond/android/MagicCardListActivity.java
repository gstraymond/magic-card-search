package fr.gstraymond.android;

import android.app.ActionBar;
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
import fr.gstraymond.R;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;
import fr.gstraymond.ui.EndScrollListener;
import fr.gstraymond.ui.TextListener;

/**
 * An activity representing a list of MagicCards. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link MagicCardDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MagicCardListFragment} and the item details (if present) is a
 * {@link MagicCardDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link MagicCardListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class MagicCardListActivity extends FragmentActivity implements
		MagicCardListFragment.Callbacks {
	private boolean mTwoPane;

	private TextListener textListener;
	private EndScrollListener endScrollListener;
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

		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		if (findViewById(R.id.magiccard_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((MagicCardListFragment) getSupportFragmentManager()
					.findFragmentById(R.id.magiccard_list))
					.setActivateOnItemClick(true);
		}

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.i(getClass().getName(), "onPostCreate");
		SearchOptions options = new SearchOptions().setQuery("*");
		new SearchProcessor(this, options).execute();
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
			Intent detailIntent = new Intent(this,
					MagicCardDetailActivity.class);
			detailIntent.putExtra(MagicCardDetailFragment.MAGIC_CARD, item);
			startActivity(detailIntent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.magiccard_list_menu, menu);

		
//		SearchViewCompat sv = (SearchViewCompat) SearchViewCompat.newSearchView(this);
		SearchView sv = new SearchView(this);
		sv.setOnQueryTextListener(textListener);
		menu.findItem(R.id.search_tab).setActionView(sv);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.list_tab:
			findViewById(R.id.facet_list).setVisibility(View.GONE);
			findViewById(R.id.magiccard_list).setVisibility(View.VISIBLE);
			item.setEnabled(false);
			menu.findItem(R.id.facet_tab).setEnabled(true);
			return true;

		case R.id.facet_tab:
			findViewById(R.id.magiccard_list).setVisibility(View.GONE);
			findViewById(R.id.facet_list).setVisibility(View.VISIBLE);
			item.setEnabled(false);
			menu.findItem(R.id.list_tab).setEnabled(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
}
