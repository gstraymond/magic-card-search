package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.biz.*
import fr.gstraymond.constants.Consts.CARD
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.EndScrollListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(R.layout.activity_card_list),
        CardArrayAdapter.ClickCallbacks, AutocompleteProcessor.Callbacks {

    companion object {
        val CARD_RESULT = "result"
        private val SEARCH_QUERY = "searchQuery"

        fun getIntent(context: Context, searchOptions: SearchOptions): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(Companion.SEARCH_QUERY, searchOptions)
                }
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

    var searchViewCursorAdapter = SearchViewCursorAdapter.empty(this)

    lateinit var adapter: CardArrayAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fab = find<FloatingActionButton>(R.id.fab_wishlist)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val savedSearch = savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)
                ?: intent.getParcelableExtra<SearchOptions>(SEARCH_QUERY)

        savedSearch?.run {
            currentSearch = savedSearch
            currentSearch.addToHistory = false
            if (currentSearch.query != "*") {
                searchView.setQuery(currentSearch.query, false)
            }
        }

        val (wishList, deck) = currentSearch.deckId?.run {
            null to app().cardListBuilder.build(toInt())
        } ?: (app().wishList to null)

        adapter = CardArrayAdapter(this, wishList, deck, this)

        val layoutManager = LinearLayoutManager(this)
        endScrollListener = EndScrollListener(this, layoutManager, fab)
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

        searchView = find<SearchView>(R.id.search_input).apply {
            suggestionListener = SuggestionListener(this, listOf())

            setOnQueryTextListener(textListener)
            setOnSuggestionListener(suggestionListener)
            suggestionsAdapter = searchViewCursorAdapter
        }

        savedInstanceState?.getParcelable<SearchOptions>(SEARCH_QUERY)?.let { savedSearch ->
            log.d("Restored search : " + currentSearch)
            currentSearch = savedSearch
            searchView.setQuery(currentSearch.query, false)
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

        progressBarUpdater = ProgressBarUpdater(find<ProgressBar>(R.id.progress_bar))
        actionBarSetHomeButtonEnabled(true)

        ChangeLog(this).apply {
            if (firstRun()) logDialog.show()
        }

        fab.setOnClickListener { view ->
            startActivity(Intent(view.context, ListsActivity::class.java))
        }

        currentSearch.deckId?.apply {
            fab.hide()
            title = app().deckList.getByUid(this)?.name
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()

        val resultAsString = intent.getStringExtra(CARD_RESULT)
        if (resultAsString != null) {
            UIUpdater(this, resultAsString, objectMapper).execute()
        } else {
            SearchProcessor(this, currentSearch, R.string.loading_initial).execute()
        }
    }

    override fun onResume() {
        super.onResume()
        find<View>(R.id.root_view).requestFocus()
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
                SearchProcessor(this, options, R.string.loading_clear).execute()
                searchView.clearFocus()
                return true
            }

            R.id.history_tab -> {
                startActivity {
                    Intent(this, HistoryActivity::class.java)
                }
                return true
            }

            R.id.changelog_tab -> {
                Answers.getInstance().logContentView(ContentViewEvent().putContentName("Changelog"))
                ChangeLog(this).fullLogDialog.show()
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
        outState.putParcelable(SEARCH_QUERY, currentSearch)
        super.onSaveInstanceState(outState)
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        searchViewCursorAdapter.changeCursor(results)
    }
}
