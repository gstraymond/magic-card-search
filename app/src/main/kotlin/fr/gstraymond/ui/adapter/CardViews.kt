package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.DeckLine
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
                TitleView(),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                FavoriteView(cards, clickCallbacks, context.resources),
                CostView(context)
        ))

class DeckCardViews(context: Context,
                    cards: JsonList<DeckLine>,
                    clickCallbacks: CardClickCallbacks) :
        CardViews(listOf(
                TitleView(),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                DeckItemView(cards, clickCallbacks, context.resources),
                CostView(context)
        ))

class DeckDetailCardViews(context: Context) : CardViews(listOf(
        TitleView(),
        CastingCostView(context),
        FormatView(),
        TypePTView()
))

class CardDetailViews(context: Context,
                      cards: JsonList<Card>,
                      clickCallbacks: CardClickCallbacks) :
        CardViews(listOf(
                FavoriteView(cards, clickCallbacks, context.resources)
        ))

interface CardClickCallbacks {
    fun itemAdded(position: Int)

    fun itemRemoved(position: Int)
}