package fr.gstraymond.network

import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ElasticSearchApi {
    @GET("mtg/card/_search")
    fun search(@Query("source") source: String): Call<SearchResult>

    @GET("mtg/card/_search")
    fun resolve(@Query("q") query: String,
                @Query("size") size: Int = 10): Call<SearchResult>

    @GET("autocomplete/card/_search")
    fun autocomplete(@Query("source") source: String): Call<AutocompleteResult>
}