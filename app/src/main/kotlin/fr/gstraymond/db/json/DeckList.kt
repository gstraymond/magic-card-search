package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.Deck

class DeckList(context: Context,
               moshi: Moshi) :
        JsonList<Deck>(
                context,
                MapperUtil.fromType(moshi, Deck::class.java),
                listName = "deck") {

    override fun Deck.uid() = id.toString()

    fun getLastId() = elems.map(Deck::id).max() ?: 0
}
