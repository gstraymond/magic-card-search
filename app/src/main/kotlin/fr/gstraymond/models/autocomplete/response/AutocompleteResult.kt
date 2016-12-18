package fr.gstraymond.models.autocomplete.response

data class AutocompleteResult(val suggest: Suggest) {
    companion object {
        fun empty() = AutocompleteResult(Suggest(listOf()))
    }

    fun getResults(): List<Option> = suggest.card.flatMap(Card::options)
}

data class Suggest(val card: List<Card>)

data class Card(val options: List<Option>)

data class Option(val text: String,
                  val payload: Payload)

data class Payload(val stdEditionCode: String,
                   val colors: List<String>,
                   val type: String)
