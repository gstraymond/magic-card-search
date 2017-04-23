package fr.gstraymond.biz

import fr.gstraymond.db.json.CardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckLine
import java.io.File
import java.text.Normalizer
import java.util.*

class DeckManager(private val deckList: DeckList,
                  private val cardListBuilder: CardListBuilder) {

    fun createEmptyDeck() = createDeck("Deck ${deckList.size() + 1}")

    fun createDeck(deckName: String,
                   cards: List<DeckLine> = listOf<DeckLine>()): Int {
        val deckId = deckList.getLastId() + 1
        cardListBuilder.build(deckId).save(cards)
        val deckStats = DeckStats(cards)
        deckList.addOrRemove(Deck(
                deckId,
                Date(),
                deckName,
                deckStats.colors,
                deckStats.format,
                deckStats.deck.sumBy { it.mult },
                deckStats.sideboard.sumBy { it.mult }))
        return deckId
    }

    fun delete(deck: Deck) {
        cardListBuilder.build(deck.id).clear()
        deckList.delete(deck)
    }

    fun export(deck: Deck, path: String) {
        val cardList = cardListBuilder.build(deck.id)
        File("$path/${normalizeName(deck)}.mwdeck").printWriter().use {
            it.write("// NAME : ${deck.name}\n")
            it.write("// FORMAT : ${deck.format}\n")
            cardList.map { (card, _, mult, isSideboard) ->
                val line = "$mult [] ${card.title}\n"
                if (isSideboard) it.write("SB:  $line")
                else it.write("        $line")
            }
        }
    }

    // FIXME name already exists ?
    private fun normalizeName(deck: Deck) =
            Normalizer
                    .normalize(deck.name.toLowerCase(), Normalizer.Form.NFD)
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("[^A-Za-z0-9_]".toRegex(), "")
}
