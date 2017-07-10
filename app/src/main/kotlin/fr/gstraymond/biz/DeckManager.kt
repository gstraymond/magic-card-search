package fr.gstraymond.biz

import fr.gstraymond.db.json.CardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.models.CardNotImported
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import java.io.File
import java.text.Normalizer
import java.util.*

class DeckManager(private val deckList: DeckList,
                  private val cardListBuilder: CardListBuilder) {

    fun createEmptyDeck() = createDeck("Deck ${deckList.size() + 1}")

    fun createDeck(deckName: String,
                   results: List<ImportResult> = listOf<ImportResult>()): Int {
        val deckId = deckList.getLastId() + 1
        val cards = results.filter { it is DeckLine }.map { it as DeckLine }
        val cardsNotImported = results.filter { it is CardNotImported }.map { it as CardNotImported }
        cardListBuilder.build(deckId).save(cards)
        val deckStats = DeckStats(cards)
        deckList.addOrRemove(Deck(
                id = deckId,
                timestamp = Date(),
                name = deckName,
                colors = deckStats.colors,
                format = deckStats.format,
                deckSize = deckStats.deck.sumBy { it.mult },
                sideboardSize = deckStats.sideboard.sumBy { it.mult },
                cardsNotImported = cardsNotImported))
        return deckId
    }

    fun delete(deck: Deck) {
        cardListBuilder.build(deck.id).clear()
        deckList.delete(deck)
    }

    fun export(deck: Deck, path: String): String {
        val files = File(path).listFiles().map { it.name }
        val deckName = findUniqueName(files, normalizeName(deck))
        val targetPath = "$path/$deckName"
        File(targetPath).printWriter().use {
            export(deck).forEach { line -> it.write(line + "\n") }
        }
        return targetPath
    }

    fun export(deck: Deck): List<String> {
        val lines = cardListBuilder.build(deck.id).all()
                .sortedBy { it.isSideboard }
                .map { (card, _, mult, isSideboard) ->
                    val line = "$mult [] ${card.title}"
                    if (isSideboard) "SB:  $line"
                    else "        $line"
                }
        return listOf("// NAME : ${deck.name}", "// FORMAT : ${deck.format}") + lines
    }

    private fun normalizeName(deck: Deck) =
            Normalizer
                    .normalize(deck.name.toLowerCase(), Normalizer.Form.NFD)
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("[^A-Za-z0-9_]".toRegex(), "")

    private fun findUniqueName(files: List<String>, deckName: String): String {
        val targetName = "$deckName.mwdeck"
        return if (files.contains(targetName)) {
            if (deckName.last().isDigit() && deckName.contains("_")) {
                val rootName = deckName.dropLastWhile { it != '_' }
                val counter = deckName.takeLastWhile { it != '_' }.toInt() + 1
                findUniqueName(files, "$rootName$counter")
            } else findUniqueName(files, "${deckName}_1")
        } else {
            targetName
        }
    }
}
