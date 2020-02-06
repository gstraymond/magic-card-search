package fr.gstraymond.network

import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.RulesResult
import fr.gstraymond.models.search.response.SearchResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ElasticSearchApi {
    @GET("mtg/_search")
    fun search(@Query("source") source: String,
               @Query("source_content_type") sourceContentType: String = "application/json"): Call<SearchResult>

    @GET("mtg/_search")
    fun resolve(@Query("q") query: String,
                @Query("size") size: Int = 10): Call<SearchResult>

    @GET("autocomplete/_search")
    fun autocomplete(@Query("source") source: String,
                     @Query("source_content_type") sourceContentType: String = "application/json"): Call<AutocompleteResult>

    @GET("mtg-rules/_doc/{document}")
    fun getMtgRules(@Path("document") document: String): Call<RulesResult>
}