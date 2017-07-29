package fr.gstraymond.models

import fr.gstraymond.models.search.response.Card

sealed class ImportResult

data class CardNotImported(val card: String,
                           val mult: Int,
                           val isSideboard: Boolean) : ImportResult()

data class DeckLine(val card: Card,
                    val cardTimestamp: Long,
                    val mult: Int,
                    val isSideboard: Boolean) : ImportResult()
