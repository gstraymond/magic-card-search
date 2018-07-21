package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.models.DeckCard

class DeckCardList(context: Context,
                   moshi: Moshi,
                   private val deckId: String,
                   private val deckList: DeckList) : JsonList<DeckCard>(
        context,
        MapperUtil.fromType(moshi, DeckCard::class.java),
        "deckcard",
        deckId) {
    override fun DeckCard.uid() = id()

    override fun addOrRemove(elem: DeckCard) = super.addOrRemove(elem).apply { updateDeck() }

    override fun update(elem: DeckCard) = super.update(elem).apply { updateDeck() }

    override fun delete(elem: DeckCard) = super.delete(elem).apply { updateDeck() }

    private fun updateDeck() {
        deckList.getByUid(deckId)?.apply {
            val deckStats = DeckStats(elems, isCommander())
            deckList.update(copy(
                    colors = deckStats.colors,
                    deckSize = deckStats.deckSize,
                    sideboardSize = deckStats.sideboardSize)
            )
        }
    }
}

class DeckCardListBuilder(private val context: Context,
                          private val moshi: Moshi,
                          private val deckList: DeckList) {

    var cache: DeckCardListCache? = null

    fun build(deckId: Int): DeckCardList =
            if (cache?.id == deckId) {
                cache!!.deckCardList
            } else {
                DeckCardList(context, moshi, "$deckId", deckList).apply {
                    cache = DeckCardListCache(deckId, this)
                }
            }
}

data class DeckCardListCache(val id: Int,
                             val deckCardList: DeckCardList)