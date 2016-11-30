package fr.gstraymond.db.json

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.magic.card.search.commons.json.MapperUtil
import java.util.*

class Decklist(context: Context, objectMapper: ObjectMapper) :
        JsonList<Deck>(context, MapperUtil.fromCollectionType(objectMapper, Deck::class.java), "deck") {

    override fun getId(elem: Deck) = elem.id.toString()

    fun getLastId(): Int = elems.map(Deck::id).max() ?: 0
}

data class Deck(val id: Int,
                val timestamp: Date,
                val name: String,
                val colors: List<String>,
                val format: String)