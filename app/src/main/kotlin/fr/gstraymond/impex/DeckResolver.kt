package fr.gstraymond.impex

import fr.gstraymond.models.DeckLine
import fr.gstraymond.network.ElasticSearchService
import java.util.*

class DeckResolver(val searchService: ElasticSearchService) {

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<DeckLine> {
        return deck.lines
                .map { (mult, title, isSideboard) ->
                    searchService.resolve("title:$title")?.let { result ->
                        val card = result.elem.hits.hits.find { it._source.title == title }?._source
                        deckImporterTask.publishProgress("$mult x $title", card != null)
                        card?.run {
                            DeckLine(this, Date().time, mult, isSideboard)
                        }
                    }
                }
                .filter { it != null }
                .map { it!! }
    }
}