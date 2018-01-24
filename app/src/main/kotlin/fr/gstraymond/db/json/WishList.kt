package fr.gstraymond.db.json

import android.content.Context
import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.getId

class WishList(context: Context,
               moshi: Moshi) :
        JsonList<Card>(
                context,
                MapperUtil.fromType(moshi, Card::class.java),
                listName = "wishlist") {
    override fun Card.uid() = getId()
}