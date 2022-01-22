package fr.gstraymond.impex

import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.DECK
import fr.gstraymond.utils.getParameters
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MtgArenaDeckFormat : DeckFormat {

    private val lineRegex = Regex("^(\\d+) (.+) \\(\\w+\\)( \\w+)?$")

    override fun detectFormat(lines: List<String>): Boolean =
            lines.filter { it.isNotBlank() }.all(lineRegex::matches)

    override fun split(lines: List<String>): Triple<List<String>, List<String>, List<String>> =
            Triple(lines.filter { it.isNotBlank() }, emptyList(), emptyList())

    override fun parse(line: String, board: Board): DeckTextLine {
        val values = lineRegex.matchEntire(line)!!.groupValues
        return DeckTextLine(values[1].toInt(), values[2], DECK)
    }

    override fun extractName(url: URL?, lines: List<String>): String = url?.run {
        url.getParameters()
                .values
                .plus(url.getPathSegment().last())
                .run { maxByOrNull { it.length } ?: first() }
    } ?: "Deck"
}