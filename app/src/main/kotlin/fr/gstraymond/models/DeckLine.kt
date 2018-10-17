package fr.gstraymond.models

import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.getId
import java.util.*

sealed class ImportResult

data class CardNotImported(val card: String,
                           val mult: Int,
                           val isSideboard: Boolean) : ImportResult()

data class DeckLine(val card: Card,
                    val cardTimestamp: Long,
                    val mult: Int,
                    val isSideboard: Boolean) : ImportResult() {
    fun id() = card.getId()
}

data class DeckCard(val card: Card,
                    val cardTimestamp: Long = Date().time,
                    val counts: Counts = Counts(1, 0)) {
    fun id() = card.getId()

    data class Counts(val deck: Int, val sideboard: Int)

    fun setDeckCount(count: Int) = copy(counts = counts.copy(deck = count))
    fun setSBCount(count: Int) = copy(counts = counts.copy(sideboard = count))
    fun total() = counts.deck + counts.sideboard
}
