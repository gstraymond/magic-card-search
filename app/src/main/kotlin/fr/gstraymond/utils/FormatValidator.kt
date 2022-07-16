package fr.gstraymond.utils

import fr.gstraymond.biz.Formats
import fr.gstraymond.biz.Formats.*
import fr.gstraymond.models.search.response.Card

object FormatValidator {

    private const val cardWithNoSizeRestriction = "A deck can have any number of cards named"

    fun getMaxOccurrence(card: Card,
                         format: Formats?) =
        when {
            format == null -> 99
            isBasicLand(card) -> 99
            isUnlimited(card) -> 99
            format == Commander -> 1
            format == Brawl -> 1
            isRestricted(card, format) -> 1
            else -> 4
        }

    private fun isRestricted(card: Card, format: Formats?) =
            card.formats.contains("Restricted") && format == Vintage

    private fun isUnlimited(card: Card) =
            card.description.contains(cardWithNoSizeRestriction, true)

    private fun isBasicLand(card: Card) =
            card.type.startsWith("basic land", true) ||
                card.type.startsWith("basic snow land", true)
}