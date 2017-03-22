package fr.gstraymond.models

import fr.gstraymond.models.search.response.Card

data class DeckLine(val card: Card,
                    val cardTimestamp: Long,
                    val mult: Int,
                    val isSideboard: Boolean)
