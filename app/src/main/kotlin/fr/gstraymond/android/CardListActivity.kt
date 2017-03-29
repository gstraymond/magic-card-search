package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.biz.AutocompleteProcessor
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessor
import fr.gstraymond.biz.UIUpdater
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.CardArrayData
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.startActivity
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(R.layout.activity_card_list),
        CardArrayAdapter.ClickCallbacks, AutocompleteProcessor.Callbacks {

    companion object {
        private val CARD_RESULT = "result"
        private val SEARCH_QUERY = "searchQuery"

        fun getIntent(context: Context, searchOptions: SearchOptions): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(SEARCH_QUERY, searchOptions)
                }

        fun getIntent(context: Context, result: String?): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(CARD_RESULT, result)
                }
    }

    private val log = Log(this)

    private lateinit var searchView: SearchView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var suggestionListener: SuggestionListener

    lateinit var endScrollListener: EndScrollListener

    var totalCardCount: Int = 0
    var currentSearch = SearchOptions()
    var loadingSnackbar: Snackbar? = null
    val textListener = TextListener(this, this)

    var searchViewCursorAdapter = SearchViewCursorAdapter.empty(this)

    lateinit var adapter: CardArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fab = find<FloatingActionButton>(R.id.fab_wishlist)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchView = find<SearchView>(R.id.search_input).apply {
            suggestionListener = SuggestionListener(this, listOf())

            setOnQueryTextListener(textListener)
            setOnSuggestionListener(suggestionListener)
            suggestionsAdapter = searchViewCursorAdapter
        }

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)

        val restoredSearch = savedSearch?.run {
            currentSearch = savedSearch
            currentSearch.addToHistory = false
            if (currentSearch.query != "*") {
                searchView.setQuery(currentSearch.query, false)
            }
        } != null

        val data = currentSearch.deckId?.run {
            val deck = app().deckList.getByUid(this)
            CardArrayData(
                    cards = null,
                    deck = deck!! to app().cardListBuilder.build(toInt()))
        } ?: CardArrayData(
                cards = app().wishList,
                deck = null)

        adapter = CardArrayAdapter(findViewById(R.id.coordinator_layout), data, this, loadingSnackbar)

        val layoutManager = LinearLayoutManager(this)
        endScrollListener = EndScrollListener(this, layoutManager)
        endScrollListener.fab = fab
        find<RecyclerView>(R.id.recycler_view).let {
            it.layoutManager = layoutManager
            it.adapter = adapter
            it.addOnScrollListener(endScrollListener)
            it.setOnTouchListener { _, _ ->
                when {
                    searchView.hasFocus() -> searchView.clearFocus()
                }
                false
            }
        }

        drawerLayout = find<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)

        drawerLayout.addDrawerListener(drawerToggle)
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) = searchView.clearFocus()

            override fun onDrawerClosed(drawerView: View) = searchView.clearFocus()
        })

        actionBarSetHomeButtonEnabled(true)

        ChangeLog(this).apply {
            if (firstRun()) logDialog.show()
        }

        fab.setOnClickListener { _ ->
            startActivity { ListsActivity.getIntent(this) }
        }

        currentSearch.deckId?.apply {
            fab.hide()
            title = app().deckList.getByUid(this)?.name
            endScrollListener.fab = null
        }

        val resultAsString = intent.getStringExtra(CARD_RESULT)
        if (!restoredSearch && resultAsString != null) {
            log.d("onPostCreate: resultAsString $resultAsString")
            UIUpdater(this, resultAsString, objectMapper).execute()
        } else {
            log.d("onPostCreate: currentSearch $currentSearch")
            SearchProcessor(this, currentSearch).execute()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onResume() {
        super.onResume()
        find<View>(R.id.root_view).requestFocus()
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(this, card)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.card_list_menu, menu)
        currentSearch.deckId?.apply {
            menu.findItem(R.id.changelog_tab).isVisible = false
            menu.findItem(R.id.deck_tab).isVisible = true
        }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {

            R.id.clear_tab -> {
                resetSearchView()
                val options = SearchOptions(random = true, addToHistory = false)
                SearchProcessor(this, options).execute()
                searchView.clearFocus()
                return true
            }

            R.id.history_tab -> startActivity {
                HistoryActivity.getIntent(this, currentSearch.deckId)
            }.run {
                // FIXME when going back from history without search
                currentSearch.deckId?.apply { finish() }
                true
            }

            R.id.changelog_tab -> {
                ChangeLog(this).fullLogDialog.show()
                Tracker.changelog()
                return true
            }

            R.id.deck_tab -> finish()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun resetSearchView() {
        searchView.apply {
            isIconified = true
            setQuery("", false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (currentSearch != SearchOptions()) {
            outState.putParcelable(SEARCH_QUERY, currentSearch)
        }
        super.onSaveInstanceState(outState)
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        searchViewCursorAdapter.changeCursor(results)
    }
}
