package fr.gstraymond.impex

import java.net.URL

class WishlistDeckFormat: DeckFormat {
    override fun detectFormat(lines: List<String>) =
            lines.all { it.isNotBlank() }

    override fun split(lines: List<String>) =
            lines to listOf<String>()

    override fun parse(line: String, sideboard: Boolean) =
            DeckTextLine(1, line, false)

    override fun extractName(url: URL?, lines: List<String>) =
            "Wishlist"
}