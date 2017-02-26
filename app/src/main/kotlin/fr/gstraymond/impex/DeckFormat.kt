package fr.gstraymond.impex

import java.net.URL

interface DeckFormat {

    fun detectFormat(lines: List<String>): Boolean

    fun split(lines: List<String>): Pair<List<String>, List<String>>

    fun parse(line: String, sideboard: Boolean): DeckTextLine

    fun extractName(url: URL, lines: List<String>): String
}