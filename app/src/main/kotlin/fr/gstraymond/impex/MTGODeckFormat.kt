package fr.gstraymond.impex

import fr.gstraymond.models.Board
import fr.gstraymond.utils.getParameters
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MTGODeckFormat : DeckFormat {

    private val SIDEBOARD = "sideboard"

    override fun detectFormat(lines: List<String>) = lines
            .filter(String::isNotEmpty)
            .all { it.first().isDigit() || isSideboard(it) }

    private fun isSideboard(line: String) = line.lowercase().contains(SIDEBOARD)

    override fun parse(line: String, board: Board): DeckTextLine {
        val (occ, title) = line.split(Regex(" "), 2)
        return DeckTextLine(occ.toInt(), title, board)
    }

    override fun split(lines: List<String>) = lines.filter(String::isNotEmpty).run {
        indexOfFirst { isSideboard(it) }.let { index ->
            Triple(take(index), drop(index + 1), listOf<String>())
        }
    }

    override fun extractName(url: URL?, lines: List<String>) = url?.run {
            url.getParameters()
                    .values
                    .plus(url.getPathSegment().last())
                    .run { maxByOrNull { it.length } ?: first() }
    } ?: "Deck"
}