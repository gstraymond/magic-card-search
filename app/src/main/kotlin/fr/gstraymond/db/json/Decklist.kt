package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.search.model.Deck

class Decklist(context: Context, moshi: Moshi) :
        JsonList<Deck>(context, MapperUtil.fromCollectionType(moshi, Deck::class.java), "deck") {

    override fun getId(elem: Deck) = elem.id.toString()

    fun getLastId(): Int = elems.map { it.id }.max() ?: 0
}

