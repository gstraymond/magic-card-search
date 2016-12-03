package fr.gstraymond.db.json

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.search.model.Deck

class Decklist(context: Context, objectMapper: ObjectMapper) :
        JsonList<Deck>(context, MapperUtil.fromCollectionType(objectMapper, Deck::class.java), "deck") {

    override fun getId(elem: Deck) = elem.id.toString()

    fun getLastId(): Int = elems.map { it.id }.max() ?: 0
}

