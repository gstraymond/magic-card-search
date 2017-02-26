package fr.gstraymond.ui.view.impl

import android.content.Context
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.color
import fr.gstraymond.utils.drawable
import java.util.*

class DeckItemView(cards: JsonList<DeckLine>,
                   clickCallbacks: CardClickCallbacks,
                   context: Context) : CardListView<DeckLine>(
        cards,
        clickCallbacks,
        context.resources.color(R.color.colorAccent),
        context.resources.color(R.color.colorPrimaryDark),
        context.resources.drawable(R.drawable.ic_bookmark_white_18dp),
        context.resources.drawable(R.drawable.ic_bookmark_border_white_18dp)) {

    override fun getElem(card: Card) = DeckLine(card, Date().time, 1, false)
}
