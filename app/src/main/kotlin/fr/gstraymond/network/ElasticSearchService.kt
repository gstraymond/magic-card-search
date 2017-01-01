package fr.gstraymond.network

import com.magic.card.search.commons.log.Log
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.utils.time
import retrofit2.Call
import java.io.IOException


class ElasticSearchService(val elasticSearchApi: ElasticSearchApi) {

    private val log = Log(this)

    fun search(query: String): Result<SearchResult>? = execute(query) {
        elasticSearchApi.search(it)
    }

    fun autocomplete(query: String): Result<AutocompleteResult>? = execute(query) {
        elasticSearchApi.autocomplete(it)
    }

    fun resolve(query: String): Result<SearchResult>? = execute(query) {
        elasticSearchApi.resolve(it)
    }

    private fun <A> execute(query: String, f: (String) -> Call<A>): Result<A>? {
        log.d("query : %s", query)
        try {
            val (response, duration) = time { f(query).execute() }
            return when (response.code()) {
                200 -> Result(response.body(), duration)
                else -> {
                    log.w("process: bad response %s %s", response.code(), query)
                    null
                }
            }
        } catch (e: IOException) {
            log.e("process: " + e.message, e)
            return null
        }
    }
}

data class Result<out Elem>(val elem: Elem,
                            val httpDuration: Long)
