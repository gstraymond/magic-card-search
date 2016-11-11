package fr.gstraymond.impex

import android.net.Uri

class MTGODeckFormat : DeckFormat {
    private val SIDEBOARD = "Sideboard"

    override fun detectFormat(lines: List<String>): Boolean {
        return lines.all { it.first().isDigit() || it == SIDEBOARD }
    }

    override fun parse(lines: List<String>): List<DeckLine> {
        val sideboardIndex = lines.indexOfFirst { it == SIDEBOARD }
        return lines.filterNot { it == SIDEBOARD }.mapIndexed { i, line ->
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