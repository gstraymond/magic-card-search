package fr.gstraymond.models.autocomplete.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutocompleteResult(val suggest: Suggest) {
    companion object {
        fun empty() = AutocompleteResult(Suggest(listOf()))
    }

    fun getResults(): List<Option> = suggest?.card?.flatMap {it.options ?: listOf() } ?: listOf()
}

@JsonClass(generateAdapter = true)
data class Suggest(val card: List<Card>)

@JsonClass(generateAdapter = true)
data class Card(val options: List<Option>?)

@JsonClass(generateAdapter = true)
data class Option(val text: String,
                  val _source: Payload?)

@JsonClass(generateAdapter = true)
data class Payload(val stdEditionCode: String?,
                   val colors: List<String>?,
                   val type: String?,
                   val land: List<String>?)
