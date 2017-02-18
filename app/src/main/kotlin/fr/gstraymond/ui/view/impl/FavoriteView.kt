package fr.gstraymond.ui.view.impl

import android.content.Context
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.color
import fr.gstraymond.utils.drawable

class FavoriteView(cards: JsonList<Card>,
                   clickCallbacks: CardClickCallbacks,
                   context: Context) : CardListView<Card>(
        cards,
        clickCallbacks,
        context.resources.color(R.color.colorAccent),
        context.resources.color(R.color.colorPrimaryDark),
        context.resources.drawable(R.drawable.ic_star_white_18dp),
        context.resources.drawable(R.drawable.ic_star_border_white_18dp)) {

    override fun getElem(card: Card) = card
}
