package fr.gstraymond.ui

import android.support.v7.widget.SearchView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.biz.AutocompleteProcessor.Callbacks
import fr.gstraymond.biz.AutocompleteProcessorBuilder
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.biz.SearchProcessorBuilder

class TextListener(private val dataUpdater: DataUpdater,
                   private val callbacks: Callbacks,
                   private val searchProcessorBuilder: SearchProcessorBuilder,
                   private val autocompleteProcessorBuilder: AutocompleteProcessorBuilder) : SearchView.OnQueryTextListener {

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

        autocompleteProcessorBuilder.build().execute(query)
        return true
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        dataUpdater.setSearchViewData(listOf())
        if (canSearch) {
            val options = SearchOptions(
                    query = text.replace(":", ""),
                    facets = dataUpdater.getCurrentSearch().facets)
            searchProcessorBuilder.build().execute(options)
        }
        return true
    }
}
