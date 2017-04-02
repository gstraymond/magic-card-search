package fr.gstraymond.biz

import android.content.Context
import android.os.AsyncTask
import android.view.View
import com.magic.card.search.commons.log.Log
import fr.gstraymond.android.DataUpdater
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
        UiUpdater.update(searchResult, dataUpdater, context, rootView)
        dataUpdater.setSearchAvailable(true)
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