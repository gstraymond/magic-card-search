package fr.gstraymond.models.autocomplete.request

data class AutocompleteRequest(val suggest: Suggest,
                               val query: Query = Query(),
                               val size: Int = 0) {
    companion object {
        fun withQuery(query: String) = AutocompleteRequest(Suggest(Card(query)))
    }
}

data class Query(val match_all: Any = object {})

data class Suggest(val card: Card)

data class Card(val text: String,
                val completion: Completion = Completion())

data class Completion(val size: Int = 10,
                      val field: String = "suggest",
                      val fuzzy: Fuzzy = Fuzzy())

data class Fuzzy(val fuzziness: Int = 1)