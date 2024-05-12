package fr.gstraymond.models

import com.squareup.moshi.JsonClass
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.getId
import java.util.*

sealed class ImportResult

@JsonClass(generateAdapter = true)
data class CardNotImported(val card: String,
                           val mult: Int,
                           val board: Board) : ImportResult()

data class DeckLine(val card: Card,
                    val cardTimestamp: Long,
                    val mult: Int,
                    val board: Board) : ImportResult() {
    fun id() = card.getId()
}

@JsonClass(generateAdapter = true)
data class DeckCard(val card: Card,
                    val cardTimestamp: Long = Date().time,
                    val counts: Counts = Counts(1, 0, 0)) {
    fun id() = card.getId()

    @JsonClass(generateAdapter = true)
    data class Counts(val deck: Int, val sideboard: Int, val maybe: Int)

    fun setDeckCount(count: Int) = copy(counts = counts.copy(deck = count))
    fun setSBCount(count: Int) = copy(counts = counts.copy(sideboard = count))
    fun setMaybeCount(count: Int) = copy(counts = counts.copy(maybe = count))
    fun total() = counts.deck + counts.sideboard + counts.maybe
}
