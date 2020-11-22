package fr.gstraymond.impex

import fr.gstraymond.models.CardNotImported
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import fr.gstraymond.network.ElasticSearchService
import java.util.*

class DeckResolver(private val searchService: ElasticSearchService) {

    private val specialCards = setOf(
            "By Force",
            "Circle of Protection: Art",
            "Circle of Protection: Artifacts",
            "Circle of Protection: Green",
            "Circle of Protection: Red",
            "Circle of Protection: Shadow",
            "Clock of DOOOOOOOOOOOOM!",
            "Denied!",
            "Framed!",
            "Ignite the Cloneforge!",
            "Incoming!",
            "Into the Void",
            "Into the Wilds",
            "Kaboom!",
            "Kill! Destroy!",
            "Magus of the Mind",
            "Mine, Mine, Mine!",
            "Nature's Will",
            "Rune of Protection: Green",
            "Success!",
            "The Champion",
            "The Hunter",
            "The Tyrant",
            "The Warrior",
            "To Arms!",
            "Waste Land")

    fun resolve(deck: ImportedDeck, deckImporterTask: DeckImporterTask): List<ImportResult> {
        return deck.lines
                .map { (mult, title, board) ->
                    val size = if (specialCards.contains(title)) 50 else 10
                    searchService.resolve("title:$title", size)?.let { (elem) ->
                        val card = elem.hits.hits.find { it._source.title.equals(title, ignoreCase = true) }?._source
                        card?.run {
                            deckImporterTask.publishProgress("$mult x $title", result = true)
                            DeckLine(this, Date().time, mult, board)
                        }
                    } ?: {
                        deckImporterTask.publishProgress("$mult x $title", result = false)
                        CardNotImported(title, mult, board)
                    }()
                }
    }
}