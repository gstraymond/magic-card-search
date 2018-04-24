package fr.gstraymond.android

import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.*
import android.view.View
import android.widget.ExpandableListView
import android.widget.Switch
import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.presenter.CardListPresenter
import fr.gstraymond.biz.AutocompleteProcessor
import fr.gstraymond.biz.AutocompleteProcessorBuilder
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ocr.OcrCaptureActivity
import fr.gstraymond.tools.VersionUtils
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.*
import fr.gstraymond.utils.*
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(R.layout.activity_card_list),
        AutocompleteProcessor.Callbacks {

    companion object {
        const val SEARCH_QUERY = "searchQuery"

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
    private val scanButton by lazy { find<AppCompatButton>(R.id.scan_card) }
    private val rootView by lazy { find<View>(R.id.root_view) }

    private lateinit var cardArrayAdapter: CardArrayAdapter
    private lateinit var cardLayoutManager: LinearLayoutManager
    private lateinit var cardArrayData: CardArrayData

    private val presenter = CardListPresenter(this)
    private val log = Log(javaClass)

    private val REQUEST_CAMERA_CODE = 1232

    private val clickCallback = object : CardArrayAdapter.ClickCallbacks {

        private val context = this@CardListActivity

        override fun cardClicked(card: Card) = startActivity {
            CardDetailActivity.getIntent(context, card)
        }

        override fun cardLongClicked(card: Card): Boolean {
            log.w("cardLongClicked ${card.title}")
            val title = card.getLocalizedTitle(context)
            val message = presenter.getCurrentSearch().deckId?.run {
                val deck = app().deckList.getByUid(this)!!
                val add = app().cardListBuilder.build(toInt()).addOrRemove(DeckCard(card))
                if (add) String.format(resources.getString(R.string.added_to_deck), title, deck.name)
                else String.format(resources.getString(R.string.removed_from_deck), title, deck.name)
            } ?: {
                val add = app().wishList.addOrRemove(card)
                val message = if (add) String.format(resources.getString(R.string.added_to_wishlist), title)
                else String.format(resources.getString(R.string.removed_from_wishlist), title)
                updateMenuWishlistSize()
                message
            }()
            showMessage(message)
            return true
        }
    }

    private fun showMessage(message: String) {
        presenter.getLoadingSnackbar()?.dismiss()
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
        presenter.setLoadingSnackbar(snackbar)
    }

    private val cardClickCallback = object : CardClickCallbacks {
        override fun itemAdded(position: Int) = updateMenuWishlistSize()

        override fun itemRemoved(position: Int) = updateMenuWishlistSize()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = find<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: intent.getParcelableExtra(SEARCH_QUERY)

        savedSearch?.apply {
            presenter.setCurrentSearch(this)
            if (query != "*") {
                searchView.setQuery(query, false)
            }
        }

        cardArrayData = presenter.getCurrentSearch().deckId?.run {
            val deck = app().deckList.getByUid(this)
            CardArrayData(
                    cards = null,
                    deck = deck!! to app().cardListBuilder.build(toInt()))
        } ?: CardArrayData(
                cards = app().wishList,
                deck = null)

        setArrayAdapter()

        presenter.let {
            it.searchViewCursorAdapter = searchViewCursorAdapter
            it.facetListView = facetListView
            it.searchProcessor = searchProcessor
            it.filterTextView = filterTextView
            it.resetTextView = resetTextView
            it.emptyTextView = emptyTextView
            it.longPressTextView = find(R.id.text_long_press)
            it.rootView = rootView
        }

        setLayoutManager()

        recyclerView.apply {
            setHasFixedSize(true)
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
            findView(R.id.search_close_btn).setOnClickListener {
                if (presenter.getCurrentSearch().facets.isEmpty()) {
                    searchProcessor.build().execute(SearchOptions.START_SEARCH_OPTIONS())
                    resetTextView.gone()
                } else {
                    searchProcessor.build().execute(presenter.getCurrentSearch().updateQuery(""))
                }
                clearFocus()
                setQuery("", false)
            }
        }


        drawerLayout = find(R.id.drawer_layout)
        filterTextView.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }

        resetTextView.setOnClickListener {
            searchProcessor.build().execute(SearchOptions.START_SEARCH_OPTIONS().copy(deckId = presenter.getCurrentSearch().deckId))
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
                        R.id.menu_rules -> startActivity {
                            RulesActivity.getIntent(this)
                        }
                        R.id.menu_changelog -> {
                            drawerLayout.closeDrawers()
                            ChangeLog(this).fullLogDialog.show()
                        }
                    }
                    true
                }
                val item = it.menu.findItem(R.id.menu_gallery_mode)
                item.actionView.find<Switch>(R.id.switch_gallery_mode).apply {
                    isChecked = prefs.galleryMode
                    setOnCheckedChangeListener { _, b ->
                        prefs.galleryMode = b
                        switchAdapter()
                    }
                }
            }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        scanButton.apply {
            supportBackgroundTintList = resources.colorStateList(R.color.colorAccent)
            setOnClickListener {
                if (!hasPerms(CAMERA)) {
                    requestPerms(REQUEST_CAMERA_CODE, CAMERA)
                } else {
                    startScanner()
                }
            }
        }
    }

    private fun setArrayAdapter() {
        cardArrayAdapter = when (prefs.galleryMode) {
            true -> GridCardArrayAdapter(this, clickCallback, presenter, cardArrayData)
            else -> LinearCardArrayAdapter(rootView, cardArrayData, clickCallback, cardClickCallback, presenter)
        }
        recyclerView.adapter = cardArrayAdapter
        presenter.arrayAdapter = cardArrayAdapter
    }

    private fun setLayoutManager() {
        cardLayoutManager = when (prefs.galleryMode) {
            true -> GridLayoutManager(this, 2)
            else -> LinearLayoutManager(this)
        }
        recyclerView.apply {
            layoutManager = cardLayoutManager
            addOnScrollListener(EndScrollListener(searchProcessor, presenter, cardLayoutManager))
        }
    }

    private fun startScanner() {
        startActivity {
            OcrCaptureActivity.getIntent(this, autoFocus = true, useFlash = false)
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
        rootView.requestFocus()
        cardArrayAdapter.notifyDataSetChanged()

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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_CODE -> if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startScanner()
            }
        }
    }

    private fun switchAdapter() {
        val cards = cardArrayAdapter.cards()
        setArrayAdapter()
        cardArrayAdapter.setCards(cards)
        setLayoutManager()
        cardArrayAdapter.notifyDataSetChanged()
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