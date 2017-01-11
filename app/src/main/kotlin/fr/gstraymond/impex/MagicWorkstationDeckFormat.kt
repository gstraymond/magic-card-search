package fr.gstraymond.impex

import android.net.Uri

class MagicWorkstationDeckFormat : DeckFormat {

    private val COMMENT = "//"
    private val SIDEBOARD = "SB: "

    override fun detectFormat(lines: List<String>) = lines.filter(String::isNotEmpty).all {
        it.startsWith(COMMENT) ||
                it.first().isDigit() ||
                it.startsWith(SIDEBOARD)
    }

    override fun split(lines: List<String>) = lines
            .filterNot { it.startsWith(COMMENT) }
            .filter(String::isNotEmpty)
            .partition { !it.startsWith(SIDEBOARD) }
            .run {
                first to second.map { it.replace("  ", " ").replace(SIDEBOARD, "") }
            }

    override fun parse(line: String, sideboard: Boolean): DeckLine {
        val (occ, name) = when {
            line.contains("[") && line.contains("]") -> line.split(Regex(" "), 3).run {
                get(0) to get(2)
            }
            else -> line.split(Regex(" "), 2).run {
                get(0) to get(1)
            }
        }
        return DeckLine(occ.toInt(), name, sideboard)
    }

    override fun extractName(uri: Uri, lines: List<String>): String {
        return lines
                .find { it.startsWith(COMMENT + " NAME : ") }
                ?.replace(COMMENT + " NAME : ", "")
                ?: uri.lastPathSegment
    }

}