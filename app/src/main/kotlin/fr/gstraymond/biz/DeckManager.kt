package fr.gstraymond.biz

import fr.gstraymond.biz.ExportFormat.MAGIC_WORKSTATION
import fr.gstraymond.biz.ExportFormat.MTG_ARENA
import fr.gstraymond.db.json.CardListMigrator
import fr.gstraymond.db.json.DeckCardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.models.CardNotImported
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.ImportResult
import fr.gstraymond.models.search.response.Publication
import java.io.File
import java.text.Normalizer
import java.util.*

class DeckManager(private val deckList: DeckList,
                  private val cardListBuilder: DeckCardListBuilder) {

    fun createEmptyDeck() = createDeck("Deck ${deckList.size() + 1}")

    fun createDeck(deckName: String,
                   results: List<ImportResult> = listOf(),
                   maybeFormat: String? = null): Int {
        val deckId = deckList.getLastId() + 1
        val cards = results.filter { it is DeckLine }.map { it as DeckLine }
        val mergedCards = CardListMigrator.toDeckCardList(cards)
        val cardsNotImported = results.filter { it is CardNotImported }.map { it as CardNotImported }
        cardListBuilder.build(deckId).save(mergedCards)
        val deckStats = DeckStats(mergedCards, Deck.isCommander(maybeFormat))
        deckList.addOrRemove(Deck(
                id = deckId,
                timestamp = Date(),
                name = deckName,
                maybeFormat = maybeFormat,
                colors = deckStats.colors,
                deckSize = deckStats.deckSize,
                sideboardSize = deckStats.sideboardSize,
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
            export(deck, MAGIC_WORKSTATION).forEach { line -> it.write(line + "\n") }
        }
        return targetPath
    }

    fun export(deck: Deck,
               format: ExportFormat): List<String> {
        val all = cardListBuilder.build(deck.id).all()
        when (format) {
            MAGIC_WORKSTATION -> {
                val lines = all
                        .flatMap { card ->
                            if (card.counts.deck > 0 && card.counts.sideboard > 0) {
                                listOf(card.setDeckCount(0), card.setSBCount(0))
                            } else listOf(card)
                        }
                        .sortedBy { it.counts.sideboard < 0 }
                        .map { (card, _, counts) ->
                            val line = "${counts.deck + counts.sideboard} [] ${card.title}"
                            if (counts.sideboard > 0) "SB:  $line"
                            else "        $line"
                        }
                return listOf("// NAME : ${deck.name}", "// FORMAT : ${deck.maybeFormat ?: "???"}") + lines
            }
            MTG_ARENA -> {
                return all.filter { it.counts.deck > 0 }.flatMap {
                    it.card.publications
                            .filter { it.editionCode.length == 3 && it.collectorNumber != null }
                            .sortedBy(Publication::editionReleaseDate)
                            .lastOrNull()
                            ?.run { listOf("${it.total()} ${it.card.title} (${mtgaSetMapping[editionCode] ?: editionCode}) $collectorNumber") }
                            ?: listOf()
                }
            }
        }
    }

    private val mtgaSetMapping = mapOf("DOM" to "DAR")

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

enum class ExportFormat {
    MAGIC_WORKSTATION, MTG_ARENA
}