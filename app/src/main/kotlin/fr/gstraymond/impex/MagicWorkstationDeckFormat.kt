package fr.gstraymond.impex

import fr.gstraymond.models.Board
import fr.gstraymond.utils.getPathSegment
import java.net.URL

class MagicWorkstationDeckFormat : DeckFormat {

    private val COMMENT = "//"
    private val SIDEBOARD = "SB: "
    private val MAYBEBOARD = "MB: "

    override fun detectFormat(lines: List<String>) = lines.filter(String::isNotEmpty).all {
        it.startsWith(COMMENT) ||
                it.first().isDigit() ||
                it.startsWith(SIDEBOARD) ||
                it.startsWith(MAYBEBOARD)
    }

    override fun split(lines: List<String>) = lines
            .filterNot { it.startsWith(COMMENT) }
            .filter(String::isNotEmpty)
            .partition { !it.startsWith(SIDEBOARD) && !it.startsWith(MAYBEBOARD) }
            .run {
                first to second.partition { !it.startsWith(MAYBEBOARD) }
            }
            .run {
                Triple(
                        first,
                        second.first.map { it.replace("  ", " ").replace(SIDEBOARD, "") },
                        second.second.map { it.replace("  ", " ").replace(MAYBEBOARD, "") }
                )
            }

    override fun parse(line: String, board: Board): DeckTextLine {
        val (occ, name) = when {
            line.contains("[") && line.contains("]") -> line.split(Regex(" "), 3).run {
                get(0) to get(2)
            }
            else -> line.split(Regex(" "), 2).run {
                get(0) to get(1)
            }
        }
        return DeckTextLine(occ.toInt(), name, board)
    }

    override fun extractName(url: URL?, lines: List<String>) =
            extractNameFromComments(lines)
                    ?: (url?.getPathSegment()?.last() ?: "Deck")

    private fun extractNameFromComments(lines: List<String>): String? {
        val nameComment = lines
                .find { it.startsWith("$COMMENT NAME : ") }
                ?.replace("$COMMENT NAME : ", "")
        return nameComment ?: lines.find { it.startsWith(COMMENT) }?.replace(COMMENT, "")
    }
}