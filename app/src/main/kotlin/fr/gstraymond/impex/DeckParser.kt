package fr.gstraymond.impex

import com.magic.card.search.commons.log.Log

class DeckParser {

    private val log = Log(this)

    val formats = listOf(
            MagicWizardDeckFormat(),
            MTGODeckFormat(),
            MagicWorkstationDeckFormat())

    fun parse(deckList: String): Pair<DeckFormat, List<DeckLine>>? {
        log.d("DeckParser.parse:\n$deckList")

        val lines = deckList.split("\n")
                .map { it.replace("\r", "") }
                .map { it.dropWhile { it == ' ' } }

        return if (lines.isEmpty() || lines.all(String::isEmpty)) null
        else formats
                .find { it.detectFormat(lines) }
                ?.run {
                    val (deck, sideboard) = split(lines)
                    this to deck.map { parse(it, false) } +
                            sideboard.map { parse(it, true) }
                }

    }
}