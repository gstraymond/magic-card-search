package fr.gstraymond.biz

import fr.gstraymond.biz.ExportFormat.MAGIC_WORKSTATION
import fr.gstraymond.biz.ExportFormat.MTG_ARENA
import fr.gstraymond.db.json.CardListMigrator
import fr.gstraymond.db.json.DeckCardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.models.*
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
        val cards = results.filterIsInstance<DeckLine>()
        val mergedCards = CardListMigrator.toDeckCardList(cards)
        val cardsNotImported = results.filterIsInstance<CardNotImported>()
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
                maybeboardSize = deckStats.maybeboardSize,
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
                val deckLines = all.filter { it.counts.deck > 0 }.map { "      " + cc(it, it.counts.deck) }
                val sbLines = all.filter { it.counts.sideboard > 0 }.map { "SB:  " + cc(it, it.counts.sideboard) }
                val mbLines = all.filter { it.counts.maybe > 0 }.map { "MB:  " + cc(it, it.counts.maybe) }

                return listOf("// NAME : ${deck.name}", "// FORMAT : ${deck.maybeFormat ?: "???"}") + deckLines + sbLines + mbLines
            }
            MTG_ARENA -> {
                return all.filter { it.counts.deck > 0 }.flatMap {
                    it.card.publications
                            .filter { it.editionCode.length == 3 && it.collectorNumber != null }
                            .sortedBy(Publication::editionReleaseDate)
                            .lastOrNull()
                            ?.run { listOf("${it.counts.deck} ${it.card.title} (${mtgaSetMapping[editionCode] ?: editionCode}) $collectorNumber") }
                            ?: listOf()
                }
            }
        }
    }

    private fun cc(card: DeckCard, count: Int) = "$count [] ${card.card.title}"

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