package fr.gstraymond.ui

import androidx.appcompat.widget.SearchView
import fr.gstraymond.models.autocomplete.response.Option
import fr.gstraymond.ui.TextListener.Companion.SEP

class SuggestionListener(private val searchView: SearchView,
                         var autocompleteResults: List<Option>) : SearchView.OnSuggestionListener {
    override fun onSuggestionSelect(i: Int): Boolean {
        return false
    }

    override fun onSuggestionClick(i: Int): Boolean {
        if (autocompleteResults.size > i) {
            val option = autocompleteResults[i]
            val result = when (option._source?.type) {
                null -> option.text
                else -> when {
                    option.text.contains(" ") -> "'${option.text}'"
                    else -> option.text
                }
            }
            val query = searchView.query.toString().run {
                when {
                    contains(SEP) -> split(SEP).dropLast(1).plus(result).joinToString(SEP)
                    else -> result
                }
            }

            searchView.setQuery(query + SEP, true)
            return true
        }
        return false
    }
}