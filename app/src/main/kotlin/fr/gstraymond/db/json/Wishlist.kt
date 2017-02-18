package fr.gstraymond.db.json

import com.magic.card.search.commons.json.MapperUtil
import com.squareup.moshi.Moshi
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.getId

class Wishlist(customApplication: CustomApplication, moshi: Moshi) :
        JsonList<Card>(
                customApplication,
                MapperUtil.fromCollectionType(moshi, Card::class.java),
                "wishlist") {
    override fun Card.uid() = getId()
}