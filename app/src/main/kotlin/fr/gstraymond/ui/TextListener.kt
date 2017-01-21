package fr.gstraymond.ui

import android.widget.SearchView.OnQueryTextListener
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.CardListActivity
import fr.gstraymond.biz.AutocompleteProcessor
import fr.gstraymond.biz.AutocompleteProcessor.Callbacks
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessor

class TextListener(val activity: CardListActivity,
                   val callbacks: Callbacks) : OnQueryTextListener {

    companion object {
        val SEP = "\u00A0"
    }

    var canSearch = true

    private val log = Log(this)

    override fun onQueryTextChange(text: String): Boolean {
        log.d("text: %s", text)
        if (text.isEmpty() || text.endsWith(SEP)) {
            callbacks.bindAutocompleteResults(listOf())
            return false
        }

        val query =
                if (!text.contains(SEP)) text
                else text.split(SEP).last()

        AutocompleteProcessor(activity.objectMapper, activity.customApplication.searchService, callbacks).execute(query)
        return true
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        if (canSearch) {
            val facets = activity.currentSearch.facets
            val options = SearchOptions().updateQuery(text.replace(":", "")).updateFacets(facets)
            SearchProcessor(activity, options, R.string.loading_initial).execute()
        }
        return true
    }
}
