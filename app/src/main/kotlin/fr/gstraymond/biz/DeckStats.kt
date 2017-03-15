package fr.gstraymond.biz

import fr.gstraymond.models.DeckLine
import java.math.BigDecimal
import java.math.RoundingMode

class DeckStats(cards: List<DeckLine>) {

    companion object {
        fun colorSymbols(colors: List<String>) = colors.map { Colors.mainColorsMap[it] }.sortedBy { it }.joinToString(" ")
    }

    val deck by lazy { cards.filterNot { it.isSideboard } }

    val sideboard by lazy { cards.filter { it.isSideboard } }

    val colors by lazy { deck.flatMap { it.card.colors }.distinct().filter { Colors.mainColors.contains(it) } }

    val colorSymbols by lazy { DeckStats.colorSymbols(colors) }

    val format by lazy {
        cards
                .map { it.card.formats }
                .fold(setOf<String>()) { acc, formats ->
                    if (acc.isEmpty()) formats.toSet()
                    else acc.intersect(formats)
                }
                .maxBy { Formats.ordered.indexOf(it) }
                ?: ""
    }

    val totalPrice by lazy {
        cards.fold(BigDecimal(0)) { acc, it ->
            val minPrice = BigDecimal((it.card.publications.map { it.price }.filter { it > 0 }.min() ?: 0.0))
            acc + minPrice * BigDecimal(it.mult)
        }.setScale(2, RoundingMode.CEILING).toDouble()
    }

    val deckSize by lazy { deck.sumBy { it.mult } }
    val sideboardSize by lazy { sideboard.sumBy { it.mult } }

    val manaCurve by lazy {
        deck
                .filterNot { it.card.type.run { startsWith("Land") || contains(" Land") } }
                .groupBy { Math.min(it.card.convertedManaCost, 7) }
                .mapValues { it.value.sumBy { it.mult } }
    }

    val colorDistribution by lazy {
        deck
                .flatMap { line -> line.card.colors.filter { Colors.mainColors.contains(it) }.map { it to line } }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.distinctBy { it.card }.sumBy { it.mult } }
    }

    val typeDistribution by lazy {
        deck
                .map { line ->
                    line.card.type.split(" ").run {
                        when {
                            contains("Creature") -> "Creature"
                            contains("Land") -> "Land"
                            else -> "Other"
                        }
                    } to line
                }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.sumBy { it.mult } }

    }
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