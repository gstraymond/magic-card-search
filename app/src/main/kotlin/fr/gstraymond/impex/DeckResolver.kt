package fr.gstraymond.impex

import fr.gstraymond.models.CardWithOccurrence
import fr.gstraymond.network.ElasticSearchService

class DeckResolver(val searchService: ElasticSearchService) {

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<CardWithOccurrence> {
        return deck.lines
                .map { line ->
                    searchService.resolve("title:${line.title}")?.let { result ->
                        val card = result.elem.hits.hits.find { it._source.title == line.title }?._source
                        deckImporterTask.publishProgress(line.title, card != null)
                        card?.run {
                            CardWithOccurrence(this, line.occurrence, line.isSideboard)
                        }
                    }
                }
                .filter { it != null }
                .map { it!! }
    }
}