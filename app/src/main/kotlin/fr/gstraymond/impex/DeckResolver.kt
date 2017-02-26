package fr.gstraymond.impex

import fr.gstraymond.models.DeckLine
import fr.gstraymond.network.ElasticSearchService
import java.util.*

class DeckResolver(val searchService: ElasticSearchService) {

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<DeckLine> {
        return deck.lines
                .map { line ->
                    searchService.resolve("title:${line.title}")?.let { result ->
                        val card = result.elem.hits.hits.find { it._source.title == line.title }?._source
                        deckImporterTask.publishProgress(line.title, card != null)
                        card?.run {
                            DeckLine(this, Date().time, line.mult, line.isSideboard)
                        }
                    }
                }
                .filter { it != null }
                .map { it!! }
    }
}