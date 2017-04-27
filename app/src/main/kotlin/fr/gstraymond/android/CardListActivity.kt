package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
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
import fr.gstraymond.tools.VersionUtils
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.CardArrayData
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.startActivity
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(R.layout.activity_card_list),
        AutocompleteProcessor.Callbacks {

    companion object {
        val SEARCH_QUERY = "searchQuery"

        fun getIntent(context: Context, searchOptions: SearchOptions): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(SEARCH_QUERY, searchOptions)
                }
    }

    private var drawerToggle: ActionBarDrawerToggle? = null
    private lateinit var drawerLayout: DrawerLayout

    private val autocompleteProcessor by lazy { AutocompleteProcessorBuilder(app().objectMapper, app().searchService, this) }
    private val searchProcessor by lazy { SearchProcessorBuilder(presenter, app().elasticSearchClient, this, findViewById(android.R.id.content)) }
    private val suggestionListener by lazy { SuggestionListener(searchView, listOf()) }
    private val searchViewCursorAdapter by lazy { SearchViewCursorAdapter.empty(this) }

    private val facetListView by lazy { find<ExpandableListView>(R.id.right_drawer_list) }
    private val searchView by lazy { find<SearchView>(R.id.search_input) }
    private val recyclerView by lazy { find<RecyclerView>(R.id.search_recyclerview) }
    private val filterTextView by lazy { find<TextView>(R.id.toolbar_filter) }
    private val resetTextView by lazy { find<TextView>(R.id.toolbar_reset) }
    private val emptyTextView by lazy { find<TextView>(R.id.search_empty_text) }
    private val leftNavigationView by lazy { find<NavigationView>(R.id.left_drawer) }

    private lateinit var arrayAdapter: CardArrayAdapter

    private val presenter = CardListPresenter(this)
    private val log = Log(javaClass)

    private val clickCallback = object : CardArrayAdapter.ClickCallbacks {
        override fun cardClicked(card: Card) = startActivity {
            CardDetailActivity.getIntent(this@CardListActivity, card)
        }
    }

    private val cardClickCallback = object : CardClickCallbacks {
        override fun itemAdded(position: Int) = updateMenuWishlistSize()

        override fun itemRemoved(position: Int) = updateMenuWishlistSize()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootView = findViewById(R.id.root_view)
        val toolbar = find<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)

        savedSearch?.apply {
            presenter.setCurrentSearch(this)
            if (query != "*") {
                searchView.setQuery(query, false)
            }
        }

        val data = presenter.getCurrentSearch().deckId?.run {
            val deck = app().deckList.getByUid(this)
            CardArrayData(
                    cards = null,
                    deck = deck!! to app().cardListBuilder.build(toInt()))
        } ?: CardArrayData(
                cards = app().wishList,
                deck = null)

        arrayAdapter = CardArrayAdapter(rootView, data, clickCallback, cardClickCallback, presenter)

        presenter.let {
            it.searchViewCursorAdapter = searchViewCursorAdapter
            it.arrayAdapter = arrayAdapter
            it.facetListView = facetListView
            it.searchProcessor = searchProcessor
            it.filterTextView = filterTextView
            it.resetTextView = resetTextView
            it.emptyTextView = emptyTextView
            it.rootView = rootView
        }

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = arrayAdapter
            addOnScrollListener(EndScrollListener(searchProcessor, presenter, linearLayoutManager))
            setOnTouchListener { _, _ ->
                when {
                    searchView.hasFocus() -> searchView.clearFocus()
                }
                false
            }

        }

        val textListener = TextListener(presenter, this, searchProcessor, autocompleteProcessor)
        searchView.apply {
            setOnQueryTextListener(textListener)
            setOnSuggestionListener(suggestionListener)
            suggestionsAdapter = searchViewCursorAdapter
        }


        drawerLayout = find<DrawerLayout>(R.id.drawer_layout)
        filterTextView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        resetTextView.setOnClickListener {
            searchProcessor.build().execute(SearchOptions.START_SEARCH_OPTIONS)
            searchView.apply {
                clearFocus()
                setQuery("", false)
            }
            resetTextView.gone()
        }

        actionBarSetHomeButtonEnabled(true)

        ChangeLog(this).apply {
            if (firstRun()) logDialog.show()
        }

        if (presenter.getCurrentSearch().deckId == null) {
            val actionBarDrawerToggle = ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    toolbar,
                    R.string.drawer_open,
                    R.string.drawer_close)
            drawerToggle = actionBarDrawerToggle
            drawerLayout.addDrawerListener(actionBarDrawerToggle)

            leftNavigationView.let {
                val headerView = it.getHeaderView(0)
                headerView.find<TextView>(R.id.nav_header_app_name).text = VersionUtils.getAppName(this)
                headerView.find<TextView>(R.id.nav_header_app_version).text = VersionUtils.getAppVersion()
                it.setNavigationItemSelectedListener { item ->
                    drawerLayout.closeDrawer(it, false)
                    when (item.itemId) {
                        R.id.menu_decks -> startActivity {
                            DecksActivity.getIntent(this)
                        }
                        R.id.menu_wishlist -> startActivity {
                            WishListActivity.getIntent(this)
                        }
                        R.id.menu_searches -> startActivity {
                            HistoryActivity.getIntent(this)
                        }
                        R.id.menu_changelog -> {
                            drawerLayout.closeDrawers()
                            ChangeLog(this).fullLogDialog.show()
                        }
                    }
                    true
                }
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        log.d("onCreateView: getCurrentSearch ${presenter.getCurrentSearch()}")
        searchProcessor.build().execute(presenter.getCurrentSearch().updateAddToHistory(false))

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle?.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle?.onConfigurationChanged(newConfig)
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        presenter.setSearchViewData(results)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(SEARCH_QUERY, presenter.getCurrentSearch().updateAppend(false))
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        findViewById(R.id.root_view).requestFocus()

        if (presenter.getCurrentSearch().deckId == null) {
            updateMenuWishlistSize()

            updateMenuListSize(app().deckList.size(),
                    R.id.menu_decks,
                    R.string.decks_title,
                    R.string.decks_title_number)
        }
    }

    private fun updateMenuWishlistSize() {
        updateMenuListSize(app().wishList.size(),
                R.id.menu_wishlist,
                R.string.wishlist_title,
                R.string.wishlist_title_number)
    }

    private fun updateMenuListSize(size: Int, menuId: Int, stringEmpty: Int, stringNumber: Int) {
        leftNavigationView.menu.findItem(menuId).title = when (size) {
            0 -> getString(stringEmpty)
            else -> String.format(getString(stringNumber), size)
        }
    }
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