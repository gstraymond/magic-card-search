package fr.gstraymond.impex

import fr.gstraymond.utils.getParameters
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MtgArenaDeckFormat : DeckFormat {

    private val lineRegex = Regex("^(\\d+) (.+) \\(\\w+\\)( \\w+)?$")

    override fun detectFormat(lines: List<String>): Boolean =
            lines.filter { it.isNotBlank() }.all(lineRegex::matches)

    override fun split(lines: List<String>): Pair<List<String>, List<String>> =
            lines.filter { it.isNotBlank() } to emptyList()

    override fun parse(line: String, sideboard: Boolean): DeckTextLine {
        val values = lineRegex.matchEntire(line)!!.groupValues
        return DeckTextLine(values[1].toInt(), values[2], false)
    }

    override fun extractName(url: URL?, lines: List<String>): String = url?.run {
        url.getParameters()
                .values
                .plus(url.getPathSegment().last())
                .run { maxBy { it.length } ?: first() }
    } ?: "Deck"
}