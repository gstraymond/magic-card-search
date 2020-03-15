package fr.gstraymond.impex

import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.*
import java.net.URL

class WishlistDeckFormat: DeckFormat {
    override fun detectFormat(lines: List<String>) =
            lines.all { it.isNotBlank() }

    override fun split(lines: List<String>) =
            Triple(lines, listOf<String>(), listOf<String>())

    override fun parse(line: String, board: Board) =
            DeckTextLine(1, line, DECK)

    override fun extractName(url: URL?, lines: List<String>) =
            "Wishlist"
}