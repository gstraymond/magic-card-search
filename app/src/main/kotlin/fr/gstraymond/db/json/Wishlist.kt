package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.models.search.response.Card

class Wishlist(context: Context, moshi: Moshi) :
        JsonList<Card>(context, MapperUtil.fromCollectionType(moshi, Card::class.java), "wishlist") {

    override fun getId(elem: Card): String {
        return String.format("%s %s %s", elem.title, elem.type, elem.castingCost)
    }
}