package fr.gstraymond.impex

import fr.gstraymond.utils.getParameters
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MTGODeckFormat : DeckFormat {

    private val SIDEBOARD = "sideboard"

    override fun detectFormat(lines: List<String>) = lines
            .filter(String::isNotEmpty)
            .all { it.first().isDigit() || isSideboard(it) }

    private fun isSideboard(line: String) = line.toLowerCase().contains(SIDEBOARD)

    override fun parse(line: String, sideboard: Boolean): DeckTextLine {
        val (occ, title) = line.split(Regex(" "), 2)
        return DeckTextLine(occ.toInt(), title, sideboard)
    }

    override fun split(lines: List<String>) = lines.filter(String::isNotEmpty).run {
        indexOfFirst { isSideboard(it) }.let { index ->
            take(index) to drop(index + 1)
        }
    }

    override fun extractName(url: URL?, lines: List<String>) = url?.run {
            url.getParameters()
                    .values
                    .plus(url.getPathSegment().last())
                    .run { maxBy { it.length } ?: first() }
    } ?: "Deck"
}