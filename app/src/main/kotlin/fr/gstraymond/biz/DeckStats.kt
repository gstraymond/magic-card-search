package fr.gstraymond.biz

import fr.gstraymond.db.json.CardWithOccurrence

class DeckStats(cards: List<CardWithOccurrence>) {

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