package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.CardWithOccurrence
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.impl.*

abstract class CardViews(private val displayableViews: List<DisplayableView>) {

    fun display(parentView: View, card: Card, position: Int) {
        displayableViews.forEach {
            it.display(parentView, card, position)
        }
    }
}

class WishlistCardViews(context: Context,
                        cards: JsonList<Card>,
                        clickCallbacks: CardClickCallbacks) :
        CardViews(listOf(
                TitleView(context),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                FavoriteView(cards, clickCallbacks, context),
                CostView()))

class DeckCardViews(context: Context,
                        cards: JsonList<CardWithOccurrence>,
                        clickCallbacks: CardClickCallbacks) :
        CardViews(listOf(
                TitleView(context),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                DeckItemView(cards, clickCallbacks, context),
                CostView()))


interface CardClickCallbacks {
    fun itemAdded(position: Int)

    fun itemRemoved(position: Int)
}