package fr.gstraymond.biz

import android.content.Context
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.models.Board
import fr.gstraymond.models.Board.*
import fr.gstraymond.models.DeckCard
import kotlin.math.min

class DeckStats(private val cards: List<DeckCard>,
                private val isCommander: Boolean) {

    private val log = Log(javaClass)

    companion object {
        fun colorSymbols(colors: List<String>) = colors.map { Colors.mainColorsMap[it] }.sortedBy { it }.joinToString(" ")
    }

    private fun getCount(card: DeckCard, board: Board) = when (board) {
        DECK -> card.counts.deck
        SB -> card.counts.sideboard
        MAYBE -> card.counts.maybe
    }

    private fun getDeckCount(card: DeckCard) = getCount(card, DECK)

    val deck by lazy { cards.filter { getDeckCount(it) > 0 } }

    val sideboard by lazy { cards.filter { getCount(it, SB) > 0 } }

    val maybeboard by lazy { cards.filter { getCount(it, MAYBE) > 0 } }

    val colors by lazy { deck.flatMap { it.card.colors }.distinct().filter { Colors.mainColors.contains(it) } }

    val deckPrice by lazy {
        when {
            isCommander -> formatPrice(computePrice(DECK) + computePrice(SB))
            else -> formatPrice(computePrice(DECK))
        }
    }

    val sideboardPrice by lazy {
        formatPrice(computePrice(SB))
    }

    private fun computePrice(board: Board) =
            cards.map {
                (it.card.publications.map { it.price }.filter { it > 0 }.min()
                        ?: 0.0) * getCount(it, board)
            }.sum()

    private fun formatPrice(double: Double) = "%.2f".format(double)

    val deckSize by lazy { deck.sumBy { it.counts.deck } }
    val sideboardSize by lazy { sideboard.sumBy { it.counts.sideboard } }
    val maybeboardSize by lazy { maybeboard.sumBy { it.counts.maybe } }

    val manaCurve by lazy {
        deck
                .filterNot { it.card.type.run { startsWith("Land") || contains(" Land") } }
                .groupBy { min(it.card.convertedManaCost, 7) }
                .mapValues { it.value.sumBy { getDeckCount(it) } }
    }

    fun colorDistribution(context: Context) =
            deck
                    .flatMap { line -> line.card.colors.filter { Colors.mainColors.contains(it) }.map { it to line } }
                    .groupBy { it.first }
                    .mapValues { it.value.map { it.second }.distinctBy { it.card }.sumBy { getDeckCount(it) } }
                    .mapKeys { getString(context, "color_${it.key.toLowerCase()}") }

    private fun getString(context: Context, id: String) =
            context.resources.getString(context.resources.getIdentifier(id, "string", context.packageName))

    fun typeDistribution(context: Context) =
            deck
                    .map { line ->
                        line.card.type.run {
                            context.getString(when {
                                contains("Creature", true) -> R.string.creature
                                contains("Land", true) -> R.string.land
                                contains("Instant", true) -> R.string.instant_sorcery
                                contains("Sorcery", true) -> R.string.instant_sorcery
                                else -> R.string.other
                            })
                        } to line
                    }
                    .groupBy { it.first }
                    .mapValues { it.value.map { it.second }.sumBy { getDeckCount(it) } }

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
                        secondaryType to secondaryGroup.sumBy { getDeckCount(it) }
                    }.toMap()
                    TypeCount(primaryType, primaryGroup.sumBy { getDeckCount(it) }, secondary)
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

data class TypeCount(val type: String, val count: Int, val secondaryTypes: Map<String, Int>)

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
    const val STANDARD = "Standard"
    const val MODERN = "Modern"
    const val PIONEER = "Pioneer"
    const val LEGACY = "Legacy"
    const val VINTAGE = "Vintage"
    const val COMMANDER = "Commander"
    const val BRAWL = "Brawl"
    const val PAUPER = "Pauper"
    const val PENNY = "Penny"
    val ordered = listOf(STANDARD, PIONEER, MODERN, LEGACY, VINTAGE, COMMANDER, BRAWL, PAUPER, PENNY)
}