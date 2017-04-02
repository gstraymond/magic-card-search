package fr.gstraymond.android

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import fr.gstraymond.R
import fr.gstraymond.android.adapter.CardListFragmentPagerAdapter
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.utils.find
import sheetrock.panda.changelog.ChangeLog

class CardListActivity : CustomActivity(R.layout.activity_card_list), DataUpdater {

    companion object {
        val CARD_RESULT = "result"
        val SEARCH_QUERY = "searchQuery"

        fun getIntent(context: Context, searchOptions: SearchOptions): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(SEARCH_QUERY, searchOptions)
                }

        fun getIntent(context: Context, result: String?): Intent =
                Intent(context, CardListActivity::class.java).apply {
                    putExtra(CARD_RESULT, result)
                }
    }

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout

    private var totalCardCount: Int = 0
    private var searchOptions = SearchOptions()
    private var snackbar: Snackbar? = null
    private var _searchAvailable = true

    private lateinit var fragmentPagerAdapter: CardListFragmentPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fragmentPagerAdapter = CardListFragmentPagerAdapter(supportFragmentManager, this)

        val viewPager = find<ViewPager>(R.id.viewpager)
        viewPager.adapter = fragmentPagerAdapter

        find<TabLayout>(R.id.sliding_tabs).apply {
            setupWithViewPager(viewPager)
        }


        drawerLayout = find<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close)

        drawerLayout.addDrawerListener(drawerToggle)

        actionBarSetHomeButtonEnabled(true)

        ChangeLog(this).apply {
            if (firstRun()) logDialog.show()
        }
    }

    override fun updateCards(totalCardCount: Int, cards: List<Card>) {
        setTotalItemCount(totalCardCount)
        fragmentPagerAdapter.updateCards(cards)
    }

    override fun updateFacets(result: SearchResult) {
        fragmentPagerAdapter.updateFacets(result)
    }

    override fun getCurrentSearch() = searchOptions

    override fun setCurrentSearch(searchOptions: SearchOptions) {
        this.searchOptions = searchOptions
    }

    override fun adapterItemCount() = fragmentPagerAdapter.adapterItemCount()

    override fun getTotalItemCount() = totalCardCount

    override fun setTotalItemCount(total: Int) {
        this.totalCardCount = total
    }

    override fun getLoadingSnackbar() = snackbar

    override fun setLoadingSnackbar(snackbar: Snackbar) {
        this.snackbar = snackbar
    }

    override fun setSearchViewData(options: List<Option>) {
        fragmentPagerAdapter.setSearchViewData(options)
    }

    override fun isSearchAvailable() = _searchAvailable

    override fun setSearchAvailable(searchAvailable: Boolean) {
        _searchAvailable = searchAvailable
    }

    /*

    getCurrentSearch.deckId?.apply {
        fab.hide()
        title = app().deckList.getByUid(this)?.name
        endScrollListener.fab = null
    }
*/

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

/*override fun onResume() {
    super.onResume()
    find<View>(R.id.root_view).requestFocus()
}

override fun onCreateOptionsMenu(menu: Menu): Boolean {
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