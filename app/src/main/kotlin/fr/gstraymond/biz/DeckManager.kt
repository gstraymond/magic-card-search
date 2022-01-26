package fr.gstraymond.biz

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import fr.gstraymond.biz.ExportFormat.MAGIC_WORKSTATION
import fr.gstraymond.biz.ExportFormat.MTG_ARENA
import fr.gstraymond.db.json.CardListMigrator
import fr.gstraymond.db.json.DeckCardListBuilder
import fr.gstraymond.db.json.DeckList
import fr.gstraymond.models.*
import fr.gstraymond.models.search.response.Publication
import java.nio.charset.Charset
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

    fun clone(deck: Deck, name: String) {
        val deckId = deckList.getLastId() + 1
        val cards = cardListBuilder.build(deck.id).all()
        cardListBuilder.build(deckId).save(cards)
        val deckStats = DeckStats(cards, Deck.isCommander(deck.maybeFormat))
        deckList.addOrRemove(Deck(
                id = deckId,
                timestamp = Date(),
                name = name,
                maybeFormat = deck.maybeFormat,
                colors = deckStats.colors,
                deckSize = deckStats.deckSize,
                sideboardSize = deckStats.sideboardSize,
                maybeboardSize = deckStats.maybeboardSize,
                cardsNotImported = deck.cardsNotImported))
    }

    fun delete(deck: Deck) {
        cardListBuilder.build(deck.id).clear()
        deckList.delete(deck)
    }

    fun export(deck: Deck, path: Uri, contentResolver: ContentResolver, context: Context): String {
        contentResolver.openOutputStream(path)!!.writer(Charset.defaultCharset()).use {
            export(deck, MAGIC_WORKSTATION).forEach { line -> it.write(line + "\n") }
        }
        return DocumentFile.fromSingleUri(context, path)?.name ?: ""
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
                            .filter { it1 -> it1.editionCode.length == 3 && it1.collectorNumber != null }
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

    fun normalizeName(deck: Deck) =
            Normalizer
                    .normalize(deck.name.lowercase(), Normalizer.Form.NFD)
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("[^A-Za-z0-9_]".toRegex(), "")
}

enum class ExportFormat {
    MAGIC_WORKSTATION, MTG_ARENA
}