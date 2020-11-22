package fr.gstraymond.network

import com.magic.card.search.commons.log.Log
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.RulesResult
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.utils.time
import retrofit2.Call
import java.io.IOException


class ElasticSearchService(private val elasticSearchApi: ElasticSearchApi) {

    private val log = Log(this)

    fun search(query: String): Result<SearchResult>? = execute(query) {
        elasticSearchApi.search(it)
    }

    fun autocomplete(query: String): Result<AutocompleteResult>? = execute(query) {
        elasticSearchApi.autocomplete(it)
    }

    fun resolve(query: String,
                size: Int): Result<SearchResult>? = execute(query) {
        elasticSearchApi.resolve(it.replace("!", ""), size)
    }

    fun getMtgRules(document: String): Result<RulesResult>? = execute(document) {
        elasticSearchApi.getMtgRules(it)
    }

    private fun <A> execute(query: String, f: (String) -> Call<A>): Result<A>? {
        log.d("query : %s", query)
        return try {
            val (response, duration) = time { f(query).execute() }
            when (response.code()) {
                200 -> Result(response.body(), duration)
                else -> {
                    log.w("process: bad response %s %s %s", response.code(), response.errorBody().string(), query)
                    null
                }
            }
        } catch (e: IOException) {
            log.e("process: " + e.message, e)
            null
        }
    }
}

data class Result<out Elem>(val elem: Elem,
                            val httpDuration: Long)
