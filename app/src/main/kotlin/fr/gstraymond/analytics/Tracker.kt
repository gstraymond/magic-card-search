package fr.gstraymond.analytics

import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.ContentViewEvent
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.SearchEvent
import fr.gstraymond.biz.SearchOptions
import fr.gstraymond.models.History
import fr.gstraymond.models.autocomplete.response.AutocompleteResult
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.SearchResult
import fr.gstraymond.network.Result

object Tracker {

    fun addRemoveCard(listName: String, card: Card, added: Boolean) {
        val event = CustomEvent("list_$listName")
                .putCustomAttribute("added", "$added")
                .putCustomAttribute("card", card.title)
        Answers.getInstance().logCustom(event)
    }

    fun addRemoveDeck(added: Boolean) {
        val event = CustomEvent("deck")
                .putCustomAttribute("added", "$added")
        Answers.getInstance().logCustom(event)
    }

    fun ebayCart(card: Card) {
        val event = CustomEvent("ebay").putCustomAttribute("card", card.title)
        Answers.getInstance().logCustom(event)
    }

    fun changelog() {
        Answers.getInstance().logContentView(ContentViewEvent().putContentName("Changelog"))
    }

    fun track(event: ContentViewEvent) {
        Answers.getInstance().logContentView(event)
    }

    fun autocompleteSearch(query: String, result: Result<AutocompleteResult>) {
        val event = CustomEvent("autocomplete")
                .putCustomAttribute("results", result.elem.getResults().size)
                .putCustomAttribute("http duration", result.httpDuration)
        if (query.length > 2) event.putCustomAttribute("query", query)
        Answers.getInstance().logCustom(event)
    }

    fun autocompleteClick(query: String) {
        val event = CustomEvent("autocomplete_click").putCustomAttribute("query", query)
        Answers.getInstance().logCustom(event)
    }

    fun historySearch(history: History) {
        val event = CustomEvent("history_search")
                .putCustomAttribute("favorite", "${history.isFavorite}")
        Answers.getInstance().logCustom(event)
    }

    fun historyAddRemoveFav(added: Boolean) {
        val event = CustomEvent("history_add_favorite")
                .putCustomAttribute("added", "$added")
        Answers.getInstance().logCustom(event)
    }

    fun search(options: SearchOptions, result: Result<SearchResult>) {
        val searchEvent = SearchEvent()
                .putQuery(options.query)
                .putCustomAttribute("results", result.elem.hits.total)
                .putCustomAttribute("http duration", result.httpDuration)
                .putCustomAttribute("facets", options.facets.keys.sorted().joinToString(" - "))
        Answers.getInstance().logSearch(searchEvent)
    }
}