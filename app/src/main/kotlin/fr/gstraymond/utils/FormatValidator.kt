package fr.gstraymond.utils

import fr.gstraymond.biz.Formats
import fr.gstraymond.models.search.response.Card

object FormatValidator {

    private const val cardWithNoSizeRestriction = "A deck can have any number of cards named"

    fun getMaxOccurrence(card: Card,
                         format: String?) =
        when {
            isBasicLand(card) -> 99
            isUnlimited(card) -> 99
            format == Formats.COMMANDER -> 1
            card.formats.contains("Restricted") -> 1
            else -> 4
        }

    private fun isUnlimited(card: Card) =
            card.description.contains(cardWithNoSizeRestriction, true)

    private fun isBasicLand(card: Card) =
            card.type.startsWith("basic land", true)
}