package fr.gstraymond.impex

import android.net.Uri

class MagicWizardDeckFormat : DeckFormat {

    override fun detectFormat(lines: List<String>) =
            lines.filter(String::isNotEmpty).all { it.first().isDigit() }

    override fun split(lines: List<String>): Pair<List<String>, List<String>> {
        val emptyLine = lines.indexOfLast(String::isEmpty)
        return lines.take(emptyLine + 1).filter(String::isNotEmpty) to
                lines.drop(emptyLine + 1).filter(String::isNotEmpty)
    }

    override fun parse(line: String, sideboard: Boolean): DeckLine {
        val (occ, title) = line.split(Regex(" "), 2)
        return DeckLine(occ.toInt(), title, sideboard)
    }

    override fun extractName(uri: Uri, lines: List<String>) = ""
}