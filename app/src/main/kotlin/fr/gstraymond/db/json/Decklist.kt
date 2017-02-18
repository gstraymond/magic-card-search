package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.Deck

class Decklist(customApplication: CustomApplication, moshi: Moshi) :
        JsonList<Deck>(
                customApplication,
                MapperUtil.fromCollectionType(moshi, Deck::class.java),
                "deck") {

    override fun Deck.uid() = id.toString()

    fun getLastId() = elems.map(Deck::id).max() ?: 0
}
