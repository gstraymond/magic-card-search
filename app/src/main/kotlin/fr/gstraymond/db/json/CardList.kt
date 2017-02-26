package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.models.DeckLine
import fr.gstraymond.utils.getId

class CardList(context: Context,
               moshi: Moshi,
               private val deckId: String,
               private val deckList: DeckList) : JsonList<DeckLine>(
        context,
        MapperUtil.fromType(moshi, DeckLine::class.java),
        "deck",
        deckId) {
    override fun DeckLine.uid() = card.getId()

    override fun addOrRemove(elem: DeckLine) = super.addOrRemove(elem).apply { updateDeck() }

    override fun update(elem: DeckLine) = super.update(elem).apply { updateDeck() }

    private fun updateDeck() {
        deckList.getByUid(deckId)?.apply {
            val deckStats = DeckStats(elems)
            deckList.update(
                    copy(colors = deckStats.colors, format = deckStats.format)
            )
        }
    }
}

class CardListBuilder(private val context: Context,
                      private val moshi: Moshi,
                      private val deckList: DeckList) {
    fun build(deckId: Int): CardList = CardList(context, moshi, "$deckId", deckList)
}
