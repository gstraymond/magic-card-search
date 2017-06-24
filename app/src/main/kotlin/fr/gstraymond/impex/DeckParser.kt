package fr.gstraymond.impex

import com.magic.card.search.commons.log.Log
import java.net.URL

class DeckParser {

    private val log = Log(this)

    val formats = listOf(
            MagicWizardDeckFormat(),
            MTGODeckFormat(),
            MagicWorkstationDeckFormat())

    fun parse(deckList: String, resolvedURL: URL?): ImportedDeck? {
        log.d("DeckParser.parse:\n$deckList")

        val lines = deckList.split("\n")
                .map { it.replace("\r", "") }
                .map { it.dropWhile { it == ' ' } }
                .dropLastWhile(String::isEmpty)

        return when {
            lines.isEmpty() || lines.all(String::isEmpty) -> null
            else -> formats
                    .find { it.detectFormat(lines) }
                    ?.run {
                        log.d("Formatter found:$this")
                        ImportedDeck(name = extractName(resolvedURL, lines),
                                     lines = parseLines(lines))
                    }
        }
    }

    private fun DeckFormat.parseLines(lines: List<String>): List<DeckTextLine> {
        val (deck, sideboard) = split(lines)
        return deck.map { parse(it, false) } +
                sideboard.map { parse(it, true) }
    }
}