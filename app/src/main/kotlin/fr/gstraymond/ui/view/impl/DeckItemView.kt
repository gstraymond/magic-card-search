package fr.gstraymond.ui.view.impl

import android.content.res.Resources
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.drawable
import java.util.*

class DeckItemView(cards: JsonList<DeckLine>,
                   clickCallbacks: CardClickCallbacks,
                   resources: Resources) : CardListView<DeckLine>(
        cards,
        clickCallbacks,
        resources.colorStateList(R.color.colorAccent),
        resources.colorStateList(R.color.colorPrimaryDark),
        resources.drawable(R.drawable.ic_bookmark_white_18dp),
        resources.drawable(R.drawable.ic_bookmark_border_white_18dp),
        "deck") {

    override fun getElem(card: Card) = DeckLine(card, Date().time, 1, false)
}
