package fr.gstraymond.android.presenter

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.text.Html
import android.view.View
import android.widget.ExpandableListView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.android.prefs
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.constants.FacetConst.SET
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.ui.FacetOnChildClickListener
import fr.gstraymond.ui.adapter.CardArrayAdapter
import fr.gstraymond.ui.adapter.FacetListAdapter
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

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
    lateinit var longPressTextView: TextView
    lateinit var rootView: View

    override fun updateCards(totalCardCount: Int, cards: List<Card>) {
        setTotalItemCount(totalCardCount)

        if (searchOptions.isStartSearchOptions()) {
            arrayAdapter.setCards(listOf())
        } else {
            if (prefs.galleryMode) {
                val textId = searchOptions.deckId
                        ?.run { R.string.long_press_deck_editor }
                        ?: R.string.long_press_wishlist
                longPressTextView.setText(textId)
                longPressTextView.visible()
            } else {
                longPressTextView.gone()
            }
            if (searchOptions.append) {
                arrayAdapter.appendCards(cards)
            } else {
                arrayAdapter.setCards(cards)
            }
        }
    }

    private val filterText by lazy { context.getString(R.string.filter) }

    override fun updateFacets(result: SearchResult) {
        if (!searchOptions.append) {
            if (!searchOptions.isStartSearchOptions()
                    && (searchOptions.query != "*" || searchOptions.facets.isNotEmpty())) {
                resetTextView.visible()
                emptyTextView.gone()
            } else {
                emptyTextView.visible()
                if (searchOptions.isStartSearchOptions()) {
                    result.hits.hits.firstOrNull()?._source?.publications?.maxByOrNull {
                        it.editionReleaseDate?.time ?: 0
                    }?.edition?.let { lastEdition ->
                        val text = String.format(context.getString(R.string.search_last_extension), lastEdition, result.hits.total.value)
                        snackbar?.dismiss()
                        snackbar = Snackbar.make(rootView, Html.fromHtml(text), Snackbar.LENGTH_INDEFINITE).apply {
                            setAction(R.string.snackbar_show) {
                                val facets = mapOf(SET to listOf(lastEdition))
                                searchProcessor.build().execute(SearchOptions(facets = facets, deckId = searchOptions.deckId))
                            }
                            show()
                        }
                    }
                }
            }
            filterTextView.text = searchOptions.facets.size.run {
                when (this) {
                    0 -> filterText
                    else -> "$filterText ($this)"
                }
            }

            val adapter = FacetListAdapter(result.aggregations, searchOptions, context)
            facetListView.setAdapter(adapter)

            val listener = FacetOnChildClickListener(adapter, searchOptions, searchProcessor)
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