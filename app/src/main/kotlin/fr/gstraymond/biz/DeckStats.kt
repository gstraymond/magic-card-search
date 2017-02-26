package fr.gstraymond.biz

import fr.gstraymond.models.DeckLine

class DeckStats(cards: List<DeckLine>) {

    val mainDeck = cards.filterNot { it.isSideboard }
    val sideboard = cards.filter { it.isSideboard }
    val colors = mainDeck.flatMap { it.card.colors }.distinct().filter { Colors.mainColors.contains(it) }
    val format = mainDeck
            .map { it.card.formats }
            .fold(setOf<String>()) { acc, formats ->
                if (acc.isEmpty()) formats.toSet()
                else acc.intersect(formats)
            }
            .maxBy { Formats.ordered.indexOf(it) }
            ?: ""
}


object Colors {
    val mainColors = listOf("Black", "Blue", "Green", "Red", "White")
    val mainColorsMap = mapOf(
            "Black" to "B",
            "Blue" to "U",
            "Green" to "G",
            "Red" to "R",
            "White" to "W")
}

object Formats {
    val ordered = listOf("Vintage", "Commander", "Legacy", "Modern", "Standard")
}