package fr.gstraymond.models

import fr.gstraymond.biz.Formats
import java.util.*

data class Deck(val id: Int = 0,
                val timestamp: Date,
                val name: String,
                val colors: List<String>,
                val deckSize: Int,
                val sideboardSize: Int,
                val cardsNotImported: List<CardNotImported>,
                val maybeFormat: String? = null) {
    fun isCommander() = isCommander(maybeFormat)

    companion object {
        fun isCommander(maybeFormat: String?) = when (maybeFormat) {
            Formats.BRAWL, Formats.COMMANDER -> true
            else -> false
        }
    }
}

