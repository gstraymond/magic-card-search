package fr.gstraymond.impex

import fr.gstraymond.db.json.CardWithOccurrence
import fr.gstraymond.network.ElasticSearchConnector
import fr.gstraymond.search.model.response.SearchResult

class DeckResolver(val connector: ElasticSearchConnector<SearchResult>) {

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<CardWithOccurrence> {
        return deck.lines.map { line ->
            val result = connector.connect("magic/card/_search", "q", "title:${line.title}&size=10")
            val card = result.elem.hits.hits.find { it._source.title == line.title }?._source
            deckImporterTask.publishProgress(line.title, card != null)
            CardWithOccurrence(card, line.occurrence, line.isSideboard)
        }.filter { it.card != null }
    }
}