package fr.gstraymond.impex

import android.net.Uri

interface DeckFormat {

    fun detectFormat(lines: List<String>): Boolean

    fun split(lines: List<String>): Pair<List<String>, List<String>>

    fun parse(line: String, sideboard: Boolean): DeckLine

    fun extractName(uri: Uri, lines: List<String>): String
}