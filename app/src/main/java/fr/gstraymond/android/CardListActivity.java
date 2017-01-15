package fr.gstraymond.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.magic.card.search.commons.log.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.gstraymond.R;
import fr.gstraymond.android.fragment.CardListFragment;
import fr.gstraymond.android.fragment.CardParentListFragment;
import fr.gstraymond.biz.AutocompleteProcessor;
import fr.gstraymond.biz.ProgressBarUpdater;
import fr.gstraymond.biz.SearchOptions;
import fr.gstraymond.biz.SearchProcessor;
import fr.gstraymond.biz.UIUpdater;
import fr.gstraymond.models.JsonHistory;
import fr.gstraymond.models.autocomplete.response.Option;
import fr.gstraymond.ui.EndScrollListener;
import fr.gstraymond.ui.TextListener;
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter;
import sheetrock.panda.changelog.ChangeLog;

import static fr.gstraymond.constants.Consts.CARD;

public class CardListActivity extends CustomActivity implements
        CardListFragment.Callbacks,
        AutocompleteProcessor.Callbacks {

    private static final int DRAWER_DELAY = 1200;
    private static final String CURRENT_SEARCH = "currentSearch";
    public static final String CARD_RESULT = "result";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final int HISTORY_REQUEST_CODE = 1000;

    private TextListener textListener;
    private EndScrollListener endScrollListener;
    private SearchView searchView;

    private int totalCardCount;
    private SearchOptions currentSearch;
    private Log log = new Log(this);

    private boolean isRestored = false;
    private boolean hasDeviceRotated = false;

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private Toast loadingToast;
    private ProgressBarUpdater progressBarUpdater;
    private List<Option> autocompleteResults = new ArrayList<>();

    private ChangeLog changeLog;
    private SearchViewCursorAdapter suggestionsAdapter;

    public CardListActivity() {
        super();

        this.textListener = new TextListener(this, this);
        this.endScrollListener = new EndScrollListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        replaceFragment(new CardParentListFragment(), R.id.parent_fragment);

        if (savedInstanceState != null) {
            SearchOptions savedSearch = savedInstanceState
                    .getParcelable(CURRENT_SEARCH);
            if (savedSearch != null) {
                currentSearch = savedSearch;
                isRestored = true;
                log.d("Restored search : " + currentSearch);
            }
        }

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                searchView.clearFocus();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        progressBarUpdater = new ProgressBarUpdater((ProgressBar) findViewById(R.id.progress_bar));
        actionBarSetHomeButtonEnabled(true);

        changeLog = new ChangeLog(this);
        if (changeLog.firstRun()) {
            changeLog.getLogDialog().show();
        }

        FloatingActionButton fab_wishlist = (FloatingActionButton) findViewById(R.id.fab_wishlist);
        fab_wishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), WishListActivity.class));
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        SearchView.OnSuggestionListener suggestionListener = new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                if (autocompleteResults.size() > i) {
                    String result = autocompleteResults.get(i).getText();
                    String query = searchView.getQuery().toString();
                    if (query.contains("\u00A0")) {
                        List<String> split = new ArrayList<>(Arrays.asList(query.split("\u00A0")));
                        split.remove(split.get(split.size() - 1));
                        split.add(result);
                        query = TextUtils.join("\u00A0", split);
                    } else {
                        query = result;
                    }

                    searchView.setQuery(query + "\u00A0", true);
                    return true;
                }
                return false;
            }
        };

        suggestionsAdapter = SearchViewCursorAdapter.Companion.empty(this);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
        if (getIntent().getParcelableExtra(SEARCH_QUERY) == null) {
            openDrawer();
        }

        if (findViewById(R.id.search_input) != null) {
            searchView = (SearchView) findViewById(R.id.search_input);
            searchView.setOnQueryTextListener(textListener);
            searchView.setOnSuggestionListener(suggestionListener);
            searchView.setSuggestionsAdapter(suggestionsAdapter);

            // -- hack for settings min size suggestion
            int autoCompleteTextViewID = getResources().getIdentifier("android:id/search_src_text", "id", getPackageName());
            ((AutoCompleteTextView) searchView.findViewById(autoCompleteTextViewID)).setThreshold(1);
        }

        if (currentSearch == null) {
            currentSearch = new SearchOptions();
        }

        if (getIntent().getParcelableExtra(SEARCH_QUERY) != null) {
            currentSearch = getIntent().getParcelableExtra(SEARCH_QUERY);
            searchView.setQuery(currentSearch.getQuery(), false);
            new SearchProcessor(this, currentSearch, R.string.loading_initial).execute();
        } else {
            if (isRestored) {
                currentSearch.setAppend(false);
            }
            if (!hasDeviceRotated) {
                String resultAsString = getIntent().getStringExtra(CARD_RESULT);
                if (resultAsString != null && !isRestored) {
                    new UIUpdater(this, resultAsString, getObjectMapper())
                            .execute();
                } else {
                    new SearchProcessor(this, currentSearch, R.string.loading_initial).execute();
                }
            } else {
                hasDeviceRotated = false;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        endScrollListener.setFab((FloatingActionButton) findViewById(R.id.fab_wishlist));
    }

    private void openDrawer() {
        if (searchView != null) searchView.clearFocus();
        new Handler().postDelayed(openDrawerRunnable(), DRAWER_DELAY);
    }

    private Runnable openDrawerRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        };
    }

    /**
     * Callback method from {@link CardListFragment.Callbacks} indicating that
     * the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(Parcelable card) {
        Intent intent = new Intent(this, CardDetailActivity.class);
        intent.putExtra(CARD, card);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.card_list_menu, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
        hasDeviceRotated = true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {

            case R.id.clear_tab:
                resetSearchView();
                SearchOptions options = new SearchOptions().updateRandom(true).updateAddToHistory(false);
                new SearchProcessor(this, options, R.string.loading_clear)
                        .execute();
                openDrawer();
                return true;

            case R.id.history_tab:
                startActivityForResult(new Intent(this, HistoryActivity.class), HISTORY_REQUEST_CODE);
                return true;

            case R.id.help_tab:
                startActivity(new Intent(this, HelpActivity.class));
                return true;

            case R.id.changelog_tab:
                Answers.getInstance().logContentView(new ContentViewEvent().putContentName("Changelog"));
                changeLog.getFullLogDialog().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HISTORY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    JsonHistory history = data.getExtras().getParcelable("history");

                    if (!history.getQuery().equals("*")) {
                        searchView.setQuery(history.getQuery(), false);
                    } else {
                        searchView.setQuery("", false);
                    }

                    currentSearch = new SearchOptions()
                            .updateQuery(history.getQuery())
                            .updateFacets(history.getFacets())
                            .updateAddToHistory(false);
                    new SearchProcessor(this, currentSearch, R.string.loading_initial).execute();
                }
                break;
        }
    }

    private void resetSearchView() {
        searchView.setIconified(true);
        searchView.setQuery("", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState); // FIX !!! FAILED BINDER TRANSACTION !!!  (parcel size = 705760) android.os.TransactionTooLargeException
        outState.putParcelable(CURRENT_SEARCH, currentSearch);
    }

    @Override
    public void bindAutocompleteResults(List<Option> results) {
        autocompleteResults = results;
        suggestionsAdapter.changeCursor(results);
    }

    public TextListener getTextListener() {
        return textListener;
    }

    public EndScrollListener getEndScrollListener() {
        return endScrollListener;
    }

    public SearchOptions getCurrentSearch() {
        return currentSearch;
    }

    public void setCurrentSearch(SearchOptions currentSearch) {
        this.currentSearch = currentSearch;
    }

    public int getTotalCardCount() {
        return totalCardCount;
    }

    public void setTotalCardCount(int totalCardCount) {
        this.totalCardCount = totalCardCount;
    }

    public Toast getLoadingToast() {
        return loadingToast;
    }

    public void setLoadingToast(Toast loadingToast) {
        this.loadingToast = loadingToast;
    }

    public ProgressBarUpdater getProgressBarUpdater() {
        return progressBarUpdater;
    }
}
