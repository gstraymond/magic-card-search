package fr.gstraymond.impex

import android.net.Uri

class MagicWorkstationDeckFormat : DeckFormat {
    
    private val COMMENT = "// "
    private val LINE = "        "
    private val SIDEBOARD = "SB:  "
    
    override fun detectFormat(lines: List<String>): Boolean {
        return lines.all {
            it.startsWith(COMMENT) ||
                    it.startsWith(LINE) ||
                    it.startsWith(SIDEBOARD)
        }
    }

    override fun parse(lines: List<String>): List<DeckLine> {
        return lines.filter { it.startsWith(LINE) || it.startsWith(SIDEBOARD) }.map {
            when {
                it.startsWith(LINE) -> {
                    val (occ, nope, name) = it.replace(LINE, "").split(Regex(" "), 3)
                    DeckLine(occ.toInt(), name, false)
                }
                else -> {
                    val (occ, nope, name) = it.replace(SIDEBOARD, "").split(Regex(" "), 3)
                    DeckLine(occ.toInt(), name, true)
                }
            }
        }
    }

    override fun extractName(uri: Uri, lines: List<String>): String {
        return lines
                .find { it.startsWith(COMMENT + "NAME : ") }
                ?.replace(COMMENT + "NAME : ", "")
                ?: uri.lastPathSegment
    }

}