package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.CardWithOccurrence
import fr.gstraymond.utils.getId

class CardList(customApplication: CustomApplication,
               moshi: Moshi,
               deckId: String) : JsonList<CardWithOccurrence>(
        customApplication,
        MapperUtil.fromType(moshi, CardWithOccurrence::class.java),
        "deck",
        deckId) {
    override fun CardWithOccurrence.uid() = card.getId()
}

class CardListBuilder(private val customApplication: CustomApplication,
                      private val moshi: Moshi) {
    fun build(deckId: Int): CardList = CardList(customApplication, moshi, "$deckId")
}
