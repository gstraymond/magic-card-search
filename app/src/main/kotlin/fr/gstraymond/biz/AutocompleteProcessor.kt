package fr.gstraymond.biz

import android.os.AsyncTask
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import com.squareup.moshi.Moshi
import fr.gstraymond.models.autocomplete.request.AutocompleteRequest
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.network.ElasticSearchService

class AutocompleteProcessor(private val mapperUtil: MapperUtil<AutocompleteRequest>,
                            private val searchService: ElasticSearchService,
                            private val callbacks: Callbacks) : AsyncTask<String, String, AutocompleteResult>() {

    private val log = Log(this)

    override fun doInBackground(vararg strings: String): AutocompleteResult {
        val query = strings[0]
        val q = mapperUtil.asJsonString(AutocompleteRequest.withQuery(query))
        val result = searchService.autocomplete(q) ?: return AutocompleteResult.empty()
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

class AutocompleteProcessorBuilder(moshi: Moshi,
                                   private val searchService: ElasticSearchService,
                                   private val callbacks: AutocompleteProcessor.Callbacks) {

    private val mapperUtil: MapperUtil<AutocompleteRequest> = MapperUtil.fromType(moshi, AutocompleteRequest::class.java)

    fun build() = AutocompleteProcessor(mapperUtil, searchService, callbacks)
}