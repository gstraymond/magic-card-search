package fr.gstraymond.utils

import fr.gstraymond.biz.Formats.COMMANDER
import fr.gstraymond.biz.Formats.VINTAGE
import fr.gstraymond.models.search.response.Card

object FormatValidator {

    private const val cardWithNoSizeRestriction = "A deck can have any number of cards named"

    fun getMaxOccurrence(card: Card,
                         format: String?) =
        when {
            format == null -> 99
            isBasicLand(card) -> 99
            isUnlimited(card) -> 99
            format == COMMANDER -> 1
            isRestricted(card, format) -> 1
            else -> 4
        }

    private fun isRestricted(card: Card, format: String?) =
            card.formats.contains("Restricted") && format == VINTAGE

    private fun isUnlimited(card: Card) =
            card.description.contains(cardWithNoSizeRestriction, true)

    private fun isBasicLand(card: Card) =
            card.type.startsWith("basic land", true)
}