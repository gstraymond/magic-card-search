package fr.gstraymond.impex

import fr.gstraymond.utils.getParameters
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MagicWizardDeckFormat : DeckFormat {

    override fun detectFormat(lines: List<String>) =
            lines.filter(String::isNotEmpty).all { it.first().isDigit() }

    override fun split(lines: List<String>): Pair<List<String>, List<String>> {
        val emptyLine = lines.indexOfLast(String::isEmpty).run {
            when (this) {
                -1 -> lines.size
                else -> this
            }
        }
        return lines.take(emptyLine + 1).filter(String::isNotEmpty) to
                lines.drop(emptyLine + 1).filter(String::isNotEmpty)
    }

    override fun parse(line: String, sideboard: Boolean): DeckTextLine {
        val (occ, title) = line.split(Regex(" "), 2)
        return DeckTextLine(occ.toInt(), title, sideboard)
    }

    override fun extractName(url: URL?, lines: List<String>) = url?.run {
        url.getParameters()
                .values
                .plus(url.getPathSegment().last())
                .run { maxBy { it.length } ?: first() }
    } ?: "Deck"
}