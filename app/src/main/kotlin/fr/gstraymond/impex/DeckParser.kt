package fr.gstraymond.impex

import com.magic.card.search.commons.log.Log
import fr.gstraymond.models.Board.*
import java.net.URL
import java.util.*

class DeckParser {

    private val log = Log(this)

    private val deckFormats = listOf(
            MtgArenaDeckFormat(),
            MagicWizardDeckFormat(),
            MTGODeckFormat(),
            MagicWorkstationDeckFormat())

    private val wishlistFormats = listOf(WishlistDeckFormat())

    fun parse(deckList: String,
              resolvedURL: URL?,
              wishlist: Boolean): ImportedDeck? {
        log.d("DeckParser.parse:\n$deckList")

        val lines = deckList.split("\n")
                .map { it.replace("\r", "") }
                .map { it.dropWhile { it1 -> it1 == ' ' } }
                .map { it.trim() }
                .dropLastWhile(String::isEmpty)

        return when {
            lines.isEmpty() || lines.all(String::isEmpty) -> null
            else -> (if(wishlist) wishlistFormats else deckFormats)
                    .find { it.detectFormat(lines) }
                    ?.run {
                        log.d("Formatter found:$this")
                        ImportedDeck(name = extractName(resolvedURL, lines),
                                     lines = parseLines(lines))
                    }
        }
    }

    private fun DeckFormat.parseLines(lines: List<String>): List<DeckTextLine> {
        val (deck, sideboard, maybeboard) = split(lines)
        return (deck.map { parse(it, DECK) } +
                sideboard.map { parse(it, SB) } +
                maybeboard.map { parse(it, MAYBE) }).map { line ->
            // Alchemy rename
            if (line.title.lowercase().endsWith(" (rebalanced)"))
                line.copy(
                    title =
                    "A-" + line.title
                        .replace(" (rebalanced)", "")
                        .replace(" (Rebalanced)", "")
                )
            else line
        }
    }
}