package fr.gstraymond.ui.view.impl

import android.content.res.Resources
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.drawable

class FavoriteView(cards: JsonList<Card>,
                   clickCallbacks: CardClickCallbacks,
                   resources: Resources) : CardListView<Card>(
        cards,
        clickCallbacks,
        resources.colorStateList(R.color.colorAccent),
        resources.colorStateList(R.color.colorPrimary),
        resources.drawable(R.drawable.ic_star_white_18dp),
        resources.drawable(R.drawable.ic_star_border_white_18dp),
        "wishlist") {

    override fun getElem(card: Card) = card
}
