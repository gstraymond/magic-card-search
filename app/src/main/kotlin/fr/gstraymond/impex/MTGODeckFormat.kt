package fr.gstraymond.impex

import android.net.Uri

class MTGODeckFormat : DeckFormat {

    private val SIDEBOARD = "sideboard"

    override fun detectFormat(lines: List<String>) = lines
            .filter(String::isNotEmpty)
            .all { it.first().isDigit() || isSideboard(it) }

    private fun isSideboard(line: String) = line.toLowerCase().contains(SIDEBOARD)

    override fun parse(line: String, sideboard: Boolean): DeckLine {
        val (occ, title) = line.split(Regex(" "), 2)
        return DeckLine(occ.toInt(), title, sideboard)
    }

    override fun split(lines: List<String>): Pair<List<String>, List<String>> {
        return lines.filter(String::isNotEmpty).run {
            val sideboardIndex = lines.indexOfFirst { isSideboard(it) }
            take(sideboardIndex) to drop(sideboardIndex + 1)
        }
    }

    override fun extractName(uri: Uri, lines: List<String>): String {
        val candidates =
                uri.queryParameterNames
                        .map { uri.getQueryParameter(it) }
                        .plus(uri.lastPathSegment)
        return candidates.maxBy { it.length } ?: candidates.first()
    }
}