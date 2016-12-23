package fr.gstraymond.network

import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ElasticSearchService {
    @GET("magic/card/_search")
    fun search(@Query("source") source: String): Call<SearchResult>

    @GET("magic/card/_search")
    fun resolve(@Query("q") query: String, size: Int = 10): Call<AutocompleteResult>

    @GET("autocomplete/card/_search")
    fun autocomplete(@Query("source") source: String): Call<AutocompleteResult>
}