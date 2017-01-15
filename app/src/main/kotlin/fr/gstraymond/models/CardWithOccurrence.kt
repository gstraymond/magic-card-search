package fr.gstraymond.models

import fr.gstraymond.models.search.response.Card

data class CardWithOccurrence(val card: Card,
                              val occurrence: Int,
                              val isSideboard: Boolean)
