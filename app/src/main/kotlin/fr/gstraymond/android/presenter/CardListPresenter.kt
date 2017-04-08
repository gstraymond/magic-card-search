package fr.gstraymond.android.presenter

import android.content.Context
import android.support.design.widget.Snackbar
import android.widget.ExpandableListView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.ui.FacetOnChildClickListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.FacetListAdapter
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.hide
import fr.gstraymond.utils.show

class CardListPresenter(private val context: Context) : DataUpdater {

    private var totalCardCount: Int = 0
    private var searchOptions = SearchOptions()
    private var snackbar: Snackbar? = null
    private var _searchAvailable = true

    lateinit var searchViewCursorAdapter: SearchViewCursorAdapter
    lateinit var arrayAdapter: CardArrayAdapter
    lateinit var facetListView: ExpandableListView
    lateinit var searchProcessor: SearchProcessorBuilder
    lateinit var filterTextView: TextView
    lateinit var resetTextView: TextView
    lateinit var emptyTextView: TextView

    override fun updateCards(totalCardCount: Int, cards: List<Card>) {
        setTotalItemCount(totalCardCount)
        if (getCurrentSearch().append) {
            arrayAdapter.appendCards(cards)
        } else {
            arrayAdapter.setCards(cards)
        }
    }

    private val filterText by lazy { context.getString(R.string.filter) }

    override fun updateFacets(result: SearchResult) {
        if (!getCurrentSearch().append) {
            if (result.hits.hits.isNotEmpty()) {
                resetTextView.show()
                emptyTextView.hide()
            } else {
                emptyTextView.show()
            }
            filterTextView.text = getCurrentSearch().facets.size.run {
                when (this) {
                    0 -> filterText
                    else -> "$filterText ($this)"
                }
            }

            val adapter = FacetListAdapter(result.facets, getCurrentSearch(), context)
            facetListView.setAdapter(adapter)

            val listener = FacetOnChildClickListener(adapter, getCurrentSearch(), searchProcessor)
            facetListView.setOnChildClickListener(listener)
        }
    }

    override fun getCurrentSearch() = searchOptions

    override fun setCurrentSearch(searchOptions: SearchOptions) {
        this.searchOptions = searchOptions
    }

    override fun adapterItemCount() = arrayAdapter.itemCount

    override fun getTotalItemCount() = totalCardCount

    override fun setTotalItemCount(total: Int) {
        this.totalCardCount = total
    }

    override fun getLoadingSnackbar() = snackbar

    override fun setLoadingSnackbar(snackbar: Snackbar) {
        this.snackbar = snackbar
    }

    override fun setSearchViewData(options: List<Option>) {
        searchViewCursorAdapter.changeCursor(options)
    }

    override fun isSearchAvailable() = _searchAvailable

    override fun setSearchAvailable(searchAvailable: Boolean) {
        _searchAvailable = searchAvailable
    }

}