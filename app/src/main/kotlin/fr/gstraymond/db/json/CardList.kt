package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.DeckLine
import fr.gstraymond.utils.getId

class CardList(customApplication: CustomApplication,
               moshi: Moshi,
               deckId: String) : JsonList<DeckLine>(
        customApplication,
        MapperUtil.fromType(moshi, DeckLine::class.java),
        "deck",
        deckId) {
    override fun DeckLine.uid() = card.getId()
}

class CardListBuilder(private val customApplication: CustomApplication,
                      private val moshi: Moshi) {
    fun build(deckId: Int): CardList = CardList(customApplication, moshi, "$deckId")
}
