package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.magic.card.search.commons.log.Log
import com.squareup.moshi.Moshi
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.DeckLine

class CardList(context: Context,
               moshi: Moshi,
               deckId: String) : JsonList<DeckLine>(
        context,
        MapperUtil.fromType(moshi, DeckLine::class.java),
        "deck",
        deckId) {
    override fun DeckLine.uid() = id()
}

class CardListBuilder(private val context: Context,
                      private val moshi: Moshi) {
    fun build(deckId: Int): CardList = CardList(context, moshi, "$deckId")
}

object CardListMigrator {

    private fun toDeckCard(line: DeckLine): DeckCard {
        val counts = if (line.isSideboard) DeckCard.Counts(deck = 0, sideboard = line.mult)
        else DeckCard.Counts(deck = line.mult, sideboard = 0)
        return DeckCard(card = line.card,
                cardTimestamp = line.cardTimestamp,
                counts = counts)
    }

    fun toDeckCardList(cards: List<DeckLine>): List<DeckCard> =
            cards.fold(listOf(), { acc, line ->
                val card = toDeckCard(line)
                acc.find { it.id() == card.id() }?.run {
                    val updatedCount = if (card.counts.deck > 0) counts.copy(deck = card.counts.deck)
                    else counts.copy(sideboard = card.counts.sideboard)
                    acc.filter { it.id() != card.id() } + copy(counts = updatedCount)
                } ?: acc + card
            })

    fun migrate(context: Context,
                moshi: Moshi,
                deckList: DeckList) {
        val oldListBuilder = CardListBuilder(context, moshi)
        context.filesDir
                .listFiles()
                .map { it.name }
                .filter { it.startsWith("deck_") }
                .forEach { file ->
                    val deckId = file.drop("deck_".length).toInt()
                    val cardList = oldListBuilder.build(deckId)
                    val newListBuilder = DeckCardListBuilder(context, moshi, deckList)
                    newListBuilder.build(deckId).save(toDeckCardList(cardList.all()))
                    cardList.clear()
        }
    }
}