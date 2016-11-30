package fr.gstraymond.impex

import android.net.Uri

interface DeckFormat {

    fun detectFormat(lines: List<String>): Boolean

    fun parse(lines: List<String>): List<DeckLine>

    fun extractName(uri: Uri, lines: List<String>): String
}