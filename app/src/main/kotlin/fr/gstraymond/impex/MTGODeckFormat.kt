package fr.gstraymond.impex

import android.net.Uri

class MTGODeckFormat : DeckFormat {
    private val SIDEBOARD = "sideboard"

    override fun detectFormat(lines: List<String>): Boolean {
        return lines.all { it.first().isDigit() || isSideboard(it) }
    }

    private fun isSideboard(line: String) = line.toLowerCase().contains(SIDEBOARD)

    override fun parse(lines: List<String>): List<DeckLine> {
        val sideboardIndex = lines.indexOfFirst { isSideboard(it) }
        return lines.filterNot { isSideboard(it) }.mapIndexed { i, line ->
            val (occ, title) = line.split(Regex(" "), 2)
            DeckLine(occ.toInt(), title, i >= sideboardIndex)
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