package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.widget.ExpandableListView
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.presenter.CardListPresenter
import fr.gstraymond.biz.AutocompleteProcessor
import fr.gstraymond.biz.AutocompleteProcessorBuilder
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
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
        AutocompleteProcessor.Callbacks, CardArrayAdapter.ClickCallbacks {

    companion object {
        val SEARCH_QUERY = "searchQuery"

        fun getIntent(context: Context, searchOptions: SearchOptions): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(SEARCH_QUERY, searchOptions)
                }
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    private val autocompleteProcessor by lazy { AutocompleteProcessorBuilder(app().objectMapper, app().searchService, this) }
    private val searchProcessor by lazy { SearchProcessorBuilder(presenter, app().elasticSearchClient, this, findViewById(android.R.id.content)) }
    private val suggestionListener by lazy { SuggestionListener(searchView, listOf()) }
    private val searchViewCursorAdapter by lazy { SearchViewCursorAdapter.empty(this) }

    private lateinit var facetListView: ExpandableListView
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var arrayAdapter: CardArrayAdapter
    private lateinit var filterTextView: TextView

    private val presenter = CardListPresenter(this)
    private val log = Log(javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        facetListView = find(R.id.right_drawer_list)
        searchView = find(R.id.search_input)
        recyclerView = find(R.id.search_recyclerview)
        filterTextView = find(R.id.toolbar_filter)
        val rootView = findViewById(android.R.id.content)
        val toolbar = find<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)

        savedSearch?.apply {
            presenter.setCurrentSearch(this)
        }

        val data = presenter.getCurrentSearch().deckId?.run {
            val deck = app().deckList.getByUid(this)
            CardArrayData(
                    cards = null,
                    deck = deck!! to app().cardListBuilder.build(toInt()))
        } ?: CardArrayData(
                cards = app().wishList,
                deck = null)

        arrayAdapter = CardArrayAdapter(rootView, data, this, presenter)

        presenter.let {
            it.searchViewCursorAdapter = searchViewCursorAdapter
            it.arrayAdapter = arrayAdapter
            it.facetListView = facetListView
            it.searchProcessor = searchProcessor
            it.filterTextView = filterTextView
        }

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = arrayAdapter
            addOnScrollListener(EndScrollListener(searchProcessor, presenter, linearLayoutManager))
        }

        val textListener = TextListener(presenter, this, searchProcessor, autocompleteProcessor)
        searchView.apply {
            setOnQueryTextListener(textListener)
            setOnSuggestionListener(suggestionListener)
            suggestionsAdapter = searchViewCursorAdapter
        }

        drawerLayout = find<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)

        drawerLayout.addDrawerListener(drawerToggle)

        filterTextView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        actionBarSetHomeButtonEnabled(true)

        ChangeLog(this).apply {
            if (firstRun()) logDialog.show()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        log.d("onCreateView: getCurrentSearch ${presenter.getCurrentSearch()}")
        searchProcessor.build().execute(presenter.getCurrentSearch())

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        presenter.setSearchViewData(results)
    }

    override fun cardClicked(card: Card) = startActivity {
        CardDetailActivity.getIntent(this, card)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SEARCH_QUERY, presenter.getCurrentSearch())
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        findViewById(android.R.id.content).requestFocus()
    }

/*override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.card_list_menu, menu)
    getCurrentSearch.deckId?.apply {
        menu.findItem(R.id.changelog_tab).isVisible = false
        menu.findItem(R.id.deck_tab).isVisible = true
    }
    return true
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
            HistoryActivity.getIntent(this, getCurrentSearch.deckId)
        }.run {
            // FIXME when going back from history without search
            getCurrentSearch.deckId?.apply { finish() }
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
*/
}

interface DataUpdater {
    fun updateCards(totalCardCount: Int, cards: List<Card>)
    fun updateFacets(result: SearchResult)

    fun getCurrentSearch(): SearchOptions
    fun setCurrentSearch(searchOptions: SearchOptions)

    fun adapterItemCount(): Int

    fun getTotalItemCount(): Int
    fun setTotalItemCount(total: Int)

    fun getLoadingSnackbar(): Snackbar?
    fun setLoadingSnackbar(snackbar: Snackbar)

    fun setSearchViewData(options: List<Option>)

    fun isSearchAvailable(): Boolean
    fun setSearchAvailable(searchAvailable: Boolean)
}