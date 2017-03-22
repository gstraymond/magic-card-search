package fr.gstraymond.biz

import android.os.AsyncTask
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import com.squareup.moshi.Moshi
import fr.gstraymond.analytics.Tracker
import fr.gstraymond.models.autocomplete.request.AutocompleteRequest
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.network.ElasticSearchService

class AutocompleteProcessor(moshi: Moshi,
                            val searchService: ElasticSearchService,
                            val callbacks: AutocompleteProcessor.Callbacks) : AsyncTask<String, String, AutocompleteResult>() {

    private val mapperUtil = MapperUtil.fromType(moshi, AutocompleteRequest::class.java)

    private val log = Log(this)

    override fun doInBackground(vararg strings: String): AutocompleteResult {
        val query = strings[0]
        val q = mapperUtil.asJsonString(AutocompleteRequest.withQuery(query))
        val result = searchService.autocomplete(q) ?: return AutocompleteResult.empty()
        Tracker.autocompleteSearch(query, result)
        return result.elem
    }

    override fun onPostExecute(autocompleteResult: AutocompleteResult) {
        val results = autocompleteResult.getResults()
        log.d("autocomplete %s elems", results.size)
        callbacks.bindAutocompleteResults(results)
    }

    interface Callbacks {
        fun bindAutocompleteResults(results: List<Option>)
    }
}
