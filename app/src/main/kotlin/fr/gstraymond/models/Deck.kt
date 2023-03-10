package fr.gstraymond.models

import fr.gstraymond.biz.Formats
import java.util.*

data class Deck(val id: Int = 0,
                val timestamp: Date,
                val name: String,
                val colors: List<String>,
                val deckSize: Int,
                val sideboardSize: Int,
                val maybeboardSize: Int,
                val cardsNotImported: List<CardNotImported>,
                val maybeFormat: String? = null) {
    fun isCommander() = isCommander(maybeFormat)

    fun format(): Formats? = format(maybeFormat)

    companion object {
        fun isCommander(maybeFormat: String?) = when (format(maybeFormat)) {
            Formats.Brawl, Formats.Commander, Formats.DuelCommander -> true
            else -> false
        }

        fun format(maybeFormat: String?): Formats? = maybeFormat?.run { Formats.fromString(this) }
    }
}

