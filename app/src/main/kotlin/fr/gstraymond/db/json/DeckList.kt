package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.Deck

class DeckList(private val customApplication: CustomApplication,
               moshi: Moshi) :
        JsonList<Deck>(
                customApplication,
                MapperUtil.fromType(moshi, Deck::class.java),
                listName = "deck") {

    override fun Deck.uid() = id.toString()

    fun getLastId() = elems.map(Deck::id).max() ?: 0

    override fun save(elements: List<Deck>) {
        super.save(elements)
        customApplication.refreshLists()
    }
}
