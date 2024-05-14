package fr.gstraymond.models.autocomplete.request

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class AutocompleteRequest(val suggest: Suggest,
                               val query: Query = Query(),
                               val size: Int = 0) {
    companion object {
        fun withQuery(query: String) = AutocompleteRequest(Suggest(Card(query)))
    }
}

@JsonClass(generateAdapter = true)
data class Query(val match_all: Map<String,String> = HashMap())

@JsonClass(generateAdapter = true)
data class Suggest(val card: Card)

@JsonClass(generateAdapter = true)
data class Card(val text: String,
                val completion: Completion = Completion())

@JsonClass(generateAdapter = true)
data class Completion(val size: Int = 10,
                      val field: String = "suggest",
                      val fuzzy: Fuzzy = Fuzzy())

@JsonClass(generateAdapter = true)
data class Fuzzy(val fuzziness: Int = 1)