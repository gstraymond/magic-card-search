package fr.gstraymond.biz

import android.content.Context
import android.os.AsyncTask
import android.support.design.widget.Snackbar
import android.view.View
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.models.search.response.Hit
import fr.gstraymond.models.search.response.SearchResult

class SearchProcessor(private val dataUpdater: DataUpdater,
                      private val elasticSearchClient: ElasticSearchClient,
                      private val context: Context,
                      private val rootView: View) : AsyncTask<SearchOptions, Void, SearchResult?>() {
    private val log = Log(this)

    override fun doInBackground(vararg params: SearchOptions): SearchResult? {
        dataUpdater.setSearchAvailable(false)
        val options = params[0]
        dataUpdater.setCurrentSearch(options)
        val now = System.currentTimeMillis()
        val searchResult = launchSearch(options)
        log.i("search took " + (System.currentTimeMillis() - now) + "ms")
        return searchResult
    }

    override fun onPostExecute(searchResult: SearchResult?) {
        update(searchResult, dataUpdater, context, rootView)
        dataUpdater.setSearchAvailable(true)
    }

    private fun update(result: SearchResult?,
                       dataUpdater: DataUpdater,
                       context: Context,
                       rootView: View) {
        if (result == null) {
            showText(context.getString(R.string.failed_search), dataUpdater, rootView)
            return
        }

        val cards = result.hits.hits.map(Hit::_source)
        val totalCardCount = result.hits.total

        val textId =
                if (totalCardCount <= 1) R.string.progress_card_found
                else R.string.progress_cards_found

        dataUpdater.updateCards(totalCardCount, cards)
        dataUpdater.updateFacets(result)

        val text = String.format("%s/%s %s", dataUpdater.adapterItemCount(), totalCardCount, context.getString(textId))
        showText(text, dataUpdater, rootView)
    }

    private fun showText(message: String,
                         dataUpdater: DataUpdater,
                         rootView: View) {
        dataUpdater.getLoadingSnackbar()?.dismiss()
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        dataUpdater.setLoadingSnackbar(snackbar)
        snackbar.show()
    }

    private fun launchSearch(options: SearchOptions): SearchResult? {
        if (options.append) {
            options.from = dataUpdater.adapterItemCount()
        }

        val searchResult = elasticSearchClient.process(options)

        if (searchResult != null) {
            log.i(searchResult.hits.total.toString() + " cards found in " + searchResult.took + " ms")
        }

        return searchResult
    }
}

class SearchProcessorBuilder(private val dataUpdater: DataUpdater,
                             private val elasticSearchClient: ElasticSearchClient,
                             private val context: Context,
                             private val rootView: View) {
    fun build() = SearchProcessor(dataUpdater, elasticSearchClient, context, rootView)
}