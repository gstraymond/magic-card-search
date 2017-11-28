package fr.gstraymond.impex

import fr.gstraymond.models.CardNotImported
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import fr.gstraymond.network.ElasticSearchService
import java.util.*

class DeckResolver(private val searchService: ElasticSearchService) {

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<ImportResult> {
        return deck.lines
                .map { (mult, title, isSideboard) ->
                    searchService.resolve("title:$title")?.let { (elem) ->
                        val card = elem.hits.hits.find { it._source.title.equals(title, ignoreCase = true) }?._source
                        card?.run {
                            deckImporterTask.publishProgress("$mult x $title", result = true)
                            DeckLine(this, Date().time, mult, isSideboard)
                        }
                    } ?: {
                        deckImporterTask.publishProgress("$mult x $title", result = false)
                        CardNotImported(title, mult, isSideboard)
                    }()
                }
    }
}