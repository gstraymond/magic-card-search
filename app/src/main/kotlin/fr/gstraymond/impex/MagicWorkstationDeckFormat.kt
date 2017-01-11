package fr.gstraymond.impex

import android.net.Uri

class MagicWorkstationDeckFormat : DeckFormat {

    private val COMMENT = "//"
    private val SIDEBOARD = "SB: "
    
    override fun detectFormat(lines: List<String>): Boolean {
        return lines.all {
            it.startsWith(COMMENT) ||
                    it.first().isDigit() ||
                    it.startsWith(SIDEBOARD)
        }
    }

    override fun parse(lines: List<String>): List<DeckLine> {
        return lines.filterNot { it.startsWith(COMMENT) }.map {
            when {
                it.startsWith(SIDEBOARD) -> {
                    val (occ, nope, name) = it.replace("  ", " ").replace(SIDEBOARD, "").split(Regex(" "), 3)
                    DeckLine(occ.toInt(), name, true)
                }
                else -> {
                    println("it $it --> ${it.split(Regex(" "))}")
                    val (occ, nope, name) = it.split(Regex(" "), 3)
                    DeckLine(occ.toInt(), name, false)
                }
            }
        }
    }

    override fun extractName(uri: Uri, lines: List<String>): String {
        return lines
                .find { it.startsWith(COMMENT + " NAME : ") }
                ?.replace(COMMENT + " NAME : ", "")
                ?: uri.lastPathSegment
    }

}