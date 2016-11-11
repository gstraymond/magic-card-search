package fr.gstraymond.db.json

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.magic.card.search.commons.json.MapperUtil
import fr.gstraymond.search.model.response.Card

class Wishlist(context: Context, objectMapper: ObjectMapper) :
        JsonList<Card>(context, MapperUtil.fromCollectionType(objectMapper, Card::class.java), "wishlist") {

    override fun getId(elem: Card): String {
        return String.format("%s %s %s", elem.title, elem.type, elem.castingCost)
    }
}