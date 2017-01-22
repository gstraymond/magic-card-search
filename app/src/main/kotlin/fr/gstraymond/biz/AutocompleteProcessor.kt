package fr.gstraymond.biz

import android.os.AsyncTask

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import com.squareup.moshi.Moshi

import fr.gstraymond.models.autocomplete.request.AutocompleteRequest
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.network.ElasticSearchService
import fr.gstraymond.network.Result

class AutocompleteProcessor(moshi: Moshi,
                            val searchService: ElasticSearchService,
                            val callbacks: AutocompleteProcessor.Callbacks) : AsyncTask<String, String, AutocompleteResult>() {

    private val mapperUtil = MapperUtil.fromType(moshi, AutocompleteRequest::class.java)

    private val log = Log(this)

    override fun doInBackground(vararg strings: String): AutocompleteResult {
        val query = strings[0]
        val q = mapperUtil.asJsonString(AutocompleteRequest.withQuery(query))
        val result = searchService.autocomplete(q) ?: return AutocompleteResult.empty()
        track(query, result)
        return result.elem
    }

    private fun track(query: String, result: Result<AutocompleteResult>) {
        val event = CustomEvent("autocomplete")
                .putCustomAttribute("results", result.elem.getResults().size)
                .putCustomAttribute("http duration", result.httpDuration)
        if (query.length > 2) event.putCustomAttribute("query", query)
        Answers.getInstance().logCustom(event)
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
