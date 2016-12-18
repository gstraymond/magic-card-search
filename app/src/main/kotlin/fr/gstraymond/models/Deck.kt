package fr.gstraymond.models

import java.util.Date

data class Deck(val id: Int = 0,
                val timestamp: Date,
                val name: String,
                val colors: List<String>,
                val format: String)
