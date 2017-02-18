package fr.gstraymond.ui.view.impl

import android.content.Context
import fr.gstraymond.R
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.CardWithOccurrence
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.utils.color
import fr.gstraymond.utils.drawable

class DeckItemView(cards: JsonList<CardWithOccurrence>,
                   clickCallbacks: CardClickCallbacks,
                   context: Context) : CardListView<CardWithOccurrence>(
        cards,
        clickCallbacks,
        context.resources.color(R.color.colorAccent),
        context.resources.color(R.color.colorPrimaryDark),
        context.resources.drawable(R.drawable.ic_clear_white_48dp),
        context.resources.drawable(R.drawable.ic_bookmark_border_white_48dp)) {

    override fun getElem(card: Card) = CardWithOccurrence(card, 1, false)
}
