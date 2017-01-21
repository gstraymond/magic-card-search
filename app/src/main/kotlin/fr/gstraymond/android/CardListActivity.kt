package fr.gstraymond.android

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.biz.*
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.models.JsonHistory
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.find
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(), CardArrayAdapter.ClickCallbacks, AutocompleteProcessor.Callbacks {

    companion object {
        private val CURRENT_SEARCH = "currentSearch"
        val CARD_RESULT = "result"
        val SEARCH_QUERY = "searchQuery"
        val HISTORY_REQUEST_CODE = 1000
    }

    private val log = Log(this)

    private lateinit var searchView: SearchView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var suggestionListener: SuggestionListener

    lateinit var progressBarUpdater: ProgressBarUpdater
    lateinit var endScrollListener: EndScrollListener

    var totalCardCount: Int = 0
    var currentSearch = SearchOptions()
    var loadingToast: Toast? = null
    val textListener = TextListener(this, this)

    private var isRestored = false
    private var hasDeviceRotated = false

    private lateinit var changeLog: ChangeLog
    private var suggestionsAdapter = SearchViewCursorAdapter.empty(this)

    val adapter by lazy { CardArrayAdapter(this, customApplication.wishlist, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_list)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val layoutManager = LinearLayoutManager(this)
        endScrollListener = EndScrollListener(this, layoutManager, find<FloatingActionButton>(R.id.fab_wishlist))
        find<RecyclerView>(R.id.recycler_view).let {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addOnScrollListener(endScrollListener)
        }

        if (savedInstanceState != null) {
            val savedSearch = savedInstanceState.getParcelable<SearchOptions>(CURRENT_SEARCH)
            if (savedSearch != null) {
                currentSearch = savedSearch
                isRestored = true
                log.d("Restored search : " + currentSearch)
            }
        }

        searchView = find<SearchView>(R.id.search_input)
        suggestionListener = SuggestionListener(searchView, listOf())

        searchView.setOnQueryTextListener(textListener)
        searchView.setOnSuggestionListener(suggestionListener)
        searchView.suggestionsAdapter = suggestionsAdapter

        // -- hack for settings min size suggestion
        val autoCompleteTextViewID = resources.getIdentifier("android:id/search_src_text", "id", packageName)
        (searchView.find<AutoCompleteTextView>(autoCompleteTextViewID)).threshold = 1

        drawerLayout = find<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)

        drawerLayout.addDrawerListener(drawerToggle)
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
                searchView.clearFocus()
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })

        progressBarUpdater = ProgressBarUpdater(find<ProgressBar>(R.id.progress_bar))
        actionBarSetHomeButtonEnabled(true)

        changeLog = ChangeLog(this)
        if (changeLog.firstRun()) {
            changeLog.logDialog.show()
        }

        find<FloatingActionButton>(R.id.fab_wishlist).setOnClickListener { view ->
            startActivity(Intent(view.context, WishListActivity::class.java))
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
        if (intent.getParcelableExtra<Parcelable>(SEARCH_QUERY) == null) {
            openDrawer()
        }

        if (intent.getParcelableExtra<Parcelable>(SEARCH_QUERY) != null) {
            currentSearch = intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)
            searchView.setQuery(currentSearch.query, false)
            SearchProcessor(this, currentSearch, R.string.loading_initial).execute()
        } else {
            if (isRestored) {
                currentSearch.append = false
            }
            if (!hasDeviceRotated) {
                val resultAsString = intent.getStringExtra(CARD_RESULT)
                if (resultAsString != null && !isRestored) {
                    UIUpdater(this, resultAsString, objectMapper).execute()
                } else {
                    SearchProcessor(this, currentSearch, R.string.loading_initial).execute()
                }
            } else {
                hasDeviceRotated = false
            }
        }
    }

    private fun openDrawer() {
        searchView.clearFocus()
    }

    override fun cardClicked(card: Card) {
        startActivity {
            Intent(this, CardDetailActivity::class.java).apply {
                putExtra(CARD, card)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.card_list_menu, menu)
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
        hasDeviceRotated = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {

            R.id.clear_tab -> {
                resetSearchView()
                val options = SearchOptions().updateRandom(true).updateAddToHistory(false)
                SearchProcessor(this, options, R.string.loading_clear)
                        .execute()
                openDrawer()
                return true
            }

            R.id.history_tab -> {
                startActivityForResult(Intent(this, HistoryActivity::class.java), HISTORY_REQUEST_CODE)
                return true
            }

            R.id.help_tab -> {
                startActivity(Intent(this, HelpActivity::class.java))
                return true
            }

            R.id.changelog_tab -> {
                Answers.getInstance().logContentView(ContentViewEvent().putContentName("Changelog"))
                changeLog.fullLogDialog.show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            HISTORY_REQUEST_CODE -> if (resultCode == RESULT_OK) {
                val history = data.extras.getParcelable<JsonHistory>("history")

                if (history!!.query != "*") {
                    searchView.setQuery(history.query, false)
                } else {
                    searchView.setQuery("", false)
                }

                currentSearch = SearchOptions()
                        .updateQuery(history.query)
                        .updateFacets(history.facets)
                        .updateAddToHistory(false)
                SearchProcessor(this, currentSearch, R.string.loading_initial).execute()
            }
        }
    }

    private fun resetSearchView() {
        searchView.apply {
            isIconified = true
            setQuery("", false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //super.onSaveInstanceState(outState); // FIX !!! FAILED BINDER TRANSACTION !!!  (parcel size = 705760) android.os.TransactionTooLargeException
        outState.putParcelable(CURRENT_SEARCH, currentSearch)
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        suggestionsAdapter.changeCursor(results)
    }
}
