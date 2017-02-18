package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.CardWithOccurrence
import fr.gstraymond.utils.getId

class JsonDeck(customApplication: CustomApplication,
               moshi: Moshi,
               deckId: String) : JsonList<CardWithOccurrence>(
        customApplication,
        MapperUtil.fromCollectionType(moshi, CardWithOccurrence::class.java),
        deckId) {
    override fun CardWithOccurrence.uid() = card.getId()
}

class JsonDeckBuilder(private val customApplication: CustomApplication,
                      private val moshi: Moshi) {
    fun build(deckId: Int): JsonDeck = JsonDeck(customApplication, moshi, "$deckId")
}
