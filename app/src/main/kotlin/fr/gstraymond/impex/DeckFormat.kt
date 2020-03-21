package fr.gstraymond.impex

import fr.gstraymond.models.Board
import java.net.URL

interface DeckFormat {

    fun detectFormat(lines: List<String>): Boolean

    fun split(lines: List<String>): Triple<List<String>, List<String>, List<String>>

    fun parse(line: String, board: Board): DeckTextLine

    fun extractName(url: URL?, lines: List<String>): String
}