package fr.gstraymond.ui.view.impl

import android.content.res.Resources
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.drawable
import java.util.*

class DeckItemView(cards: JsonList<DeckCard>,
                   clickCallbacks: CardClickCallbacks,
                   resources: Resources,
                   private val addToSideboard: Boolean) : CardListView<DeckCard>(
        cards,
        clickCallbacks,
        resources.colorStateList(R.color.colorAccent),
        resources.colorStateList(R.color.colorPrimaryDark),
        resources.drawable(R.drawable.ic_bookmark_white_18dp),
        resources.drawable(R.drawable.ic_bookmark_border_white_18dp),
        "deck") {

    override fun getElem(card: Card) = DeckCard(
            card,
            Date().time,
            if (addToSideboard) DeckCard.Counts(deck = 0, sideboard = 1)
            else DeckCard.Counts(deck = 1, sideboard = 0)
    )
}
