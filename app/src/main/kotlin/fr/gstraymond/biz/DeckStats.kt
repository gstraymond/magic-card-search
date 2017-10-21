package fr.gstraymond.biz

import com.magic.card.search.commons.log.Log
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.DeckLine
import java.math.BigDecimal
import java.math.RoundingMode

class DeckStats(cards: List<DeckCard>) {

    private val log = Log(javaClass)

    companion object {
        fun colorSymbols(colors: List<String>) = colors.map { Colors.mainColorsMap[it] }.sortedBy { it }.joinToString(" ")
    }

    val deck by lazy { cards.filter { it.counts.deck > 0 } }

    val sideboard by lazy { cards.filter { it.counts.sideboard > 0 } }

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
                ?: "Invalid"
    }

    val totalPrice by lazy {
        cards.fold(BigDecimal(0)) { acc, it ->
            val minPrice = BigDecimal((it.card.publications.map { it.price }.filter { it > 0 }.min() ?: 0.0))
            acc + minPrice * BigDecimal(it.counts.deck)
        }.setScale(2, RoundingMode.CEILING).toDouble()
    }

    val deckSize by lazy { deck.sumBy { it.counts.deck } }
    val sideboardSize by lazy { sideboard.sumBy { it.counts.sideboard } }

    val manaCurve by lazy {
        deck
                .filterNot { it.card.type.run { startsWith("Land") || contains(" Land") } }
                .groupBy { Math.min(it.card.convertedManaCost, 7) }
                .mapValues { it.value.sumBy { it.counts.deck } }
    }

    val colorDistribution by lazy {
        deck
                .flatMap { line -> line.card.colors.filter { Colors.mainColors.contains(it) }.map { it to line } }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.distinctBy { it.card }.sumBy { it.counts.deck } }
    }

    val typeDistribution by lazy {
        deck
                .map { line ->
                    line.card.type.split(" ").run {
                        when {
                            contains("Creature") -> "Creature"
                            contains("Land") -> "Land"
                            else -> "Spell"
                        }
                    } to line
                }
                .groupBy { it.first }
                .mapValues { it.value.map { it.second }.sumBy { it.counts.deck } }
    }

    val typeCount by lazy {
        val primaryTypes =
                deck.flatMap { it.card.type.split(" — ").first().split(" ") }
                        .distinct()
                        .filterNot { it == "Basic" }

        primaryTypes
                .map { primaryType ->
                    val primaryGroup = deck.filter { it.card.type.split(" — ").first().contains(primaryType) }
                    val secondaryTypes = primaryGroup.flatMap {
                        val split = it.card.type.split(" — ")
                        if (split.size > 1) split[1].split(" ")
                        else listOf()
                    }.distinct()
                    val secondary = secondaryTypes.map { secondaryType ->
                        val secondaryGroup = primaryGroup.filter { it.card.type.contains(secondaryType) }
                        secondaryType to secondaryGroup.sumBy { it.counts.deck }
                    }.toMap()
                    TypeCount(primaryType, primaryGroup.sumBy { it.counts.deck }, secondary)
                }
                .sortedBy { -it.count }
    }

    val abilitiesCount by lazy {
        try {
            deck.flatMap { it.card.abilities ?: listOf() }.distinct().map { ability ->
                ability to deck.filter { it.card.abilities.contains(ability) }.sumBy { it.counts.deck }
            }.toMap()
        } catch (e: Exception) {
            log.e("${e.message} / $deck", e)
            mapOf<String, Int>()
        }
    }
}

data class TypeCount(val `type`: String, val count: Int, val secondaryTypes: Map<String, Int>)

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