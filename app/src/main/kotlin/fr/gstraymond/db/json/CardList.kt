package fr.gstraymond.db.json

import fr.gstraymond.models.Board.*
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.DeckLine

object CardListMigrator {

    private fun toDeckCard(line: DeckLine): DeckCard {
        val counts = when (line.board) {
            DECK -> DeckCard.Counts(deck = line.mult, sideboard = 0, maybe = 0)
            SB -> DeckCard.Counts(deck = 0, sideboard = line.mult, maybe = 0)
            MAYBE -> DeckCard.Counts(deck = 0, sideboard = 0, maybe = line.mult)
        }
        return DeckCard(card = line.card,
                cardTimestamp = line.cardTimestamp,
                counts = counts)
    }

    fun toDeckCardList(cards: List<DeckLine>): List<DeckCard> =
            cards.fold(listOf(), { acc, line ->
                val card = toDeckCard(line)
                acc.find { it.id() == card.id() }?.run {
                    val updatedCount = when (line.board) {
                        DECK -> counts.copy(deck = card.counts.deck)
                        SB -> counts.copy(sideboard = card.counts.sideboard)
                        MAYBE -> counts.copy(maybe = card.counts.maybe)
                    }
                    acc.filter { it.id() != card.id() } + copy(counts = updatedCount)
                } ?: acc + card
            })
}