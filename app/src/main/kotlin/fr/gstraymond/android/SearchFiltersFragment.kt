package fr.gstraymond.android

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import fr.gstraymond.R
import fr.gstraymond.biz.AutocompleteProcessor
import fr.gstraymond.biz.AutocompleteProcessorBuilder
import fr.gstraymond.biz.SearchProcessorBuilder
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.ui.FacetOnChildClickListener
import fr.gstraymond.ui.SuggestionListener
import fr.gstraymond.ui.TextListener
import fr.gstraymond.ui.adapter.FacetListAdapter
import fr.gstraymond.ui.adapter.SearchViewCursorAdapter
import fr.gstraymond.utils.app
import fr.gstraymond.utils.find

class SearchFiltersFragment : Fragment(), AutocompleteProcessor.Callbacks {

    private val dataUpdater by lazy { activity as CardListActivity }
    private val autocompleteProcessor by lazy { AutocompleteProcessorBuilder(activity.app().objectMapper, activity.app().searchService, this) }
    private val searchProcessor by lazy { SearchProcessorBuilder(dataUpdater, activity.app().elasticSearchClient, activity, activity.findViewById(android.R.id.content)) }
    private val suggestionListener by lazy { SuggestionListener(searchView, listOf()) }
    private val searchViewCursorAdapter by lazy { SearchViewCursorAdapter.empty(activity) }

    private lateinit var searchView: SearchView
    private lateinit var facetListView: ExpandableListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
            inflater.inflate(R.layout.fragment_search_filters, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = view.find(R.id.search_input)
        facetListView = view.find(R.id.left_drawer)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val textListener = TextListener(dataUpdater, this, searchProcessor, autocompleteProcessor)
        searchView.apply {
            setOnQueryTextListener(textListener)
            setOnSuggestionListener(suggestionListener)
            suggestionsAdapter = searchViewCursorAdapter
        }

        /*
        val restoredSearch = savedSearch?.run {
        getCurrentSearch = savedSearch
        getCurrentSearch.addToHistory = false
        if (getCurrentSearch.query != "*") {
            searchView.setQuery(getCurrentSearch.query, false)
        }
    } != null
         */
    }

    override fun bindAutocompleteResults(results: List<Option>) {
        suggestionListener.autocompleteResults = results
        setSearchViewData(results)
    }

    fun setFilters(result: SearchResult) {
        val adapter = FacetListAdapter(result.facets, dataUpdater.getCurrentSearch(), context)
        facetListView.setAdapter(adapter)

        val listener = FacetOnChildClickListener(adapter, dataUpdater.getCurrentSearch(), searchProcessor)
        facetListView.setOnChildClickListener(listener)
    }

    fun setSearchViewData(options: List<Option>) {
        searchViewCursorAdapter.changeCursor(options)
    }
}