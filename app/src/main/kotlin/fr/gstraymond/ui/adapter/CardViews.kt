package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.impl.*

abstract class CardViews(private val displayableViews: List<DisplayableView>) {

    fun display(parentView: View, card: Card, position: Int) {
        displayableViews.forEach {
            it.display(parentView, card, position)
        }
    }
}

class WishlistCardViews(context: Context) :
        CardViews(listOf(
                TitleView(),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                CostView(context)
        ))

class HandCardViews(context: Context) :
        CardViews(listOf(
                TitleView(),
                CastingCostView(context)
        ))

class FavoriteCardViews(context: Context,
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
                    cards: JsonList<DeckCard>,
                    clickCallbacks: CardClickCallbacks,
                    addToSideboard: Boolean) :
        CardViews(listOf(
                TitleView(),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                DeckItemView(cards, clickCallbacks, context.resources, addToSideboard),
                CostView(context)
        ))

class DeckDetailCardViews(context: Context) : CardViews(listOf(
        TitleView(),
        CastingCostView(context),
        CostView(context)
))

class SimpleCardViews(context: Context) : CardViews(listOf(
        TitleView(),
        CastingCostView(context),
        FormatView(),
        TypePTView()
))

class ShareCardDialogViews(context: Context,
                           cards: JsonList<Card>,
                           clickCallbacks: CardClickCallbacks) : CardViews(listOf(
        TitleView(),
        CastingCostView(context),
        FormatView(),
        TypePTView(),
        FavoriteView(cards, clickCallbacks, context.resources)
))

class CardDetailViews(app: CustomApplication,
                      context: Context,
                      rootView: View,
                      shareViewCallbacks: ShareView.ShareViewCallbacks) :
        CardViews(listOf(
                ShareView(app, context, rootView, null, shareViewCallbacks)
        ))

interface CardClickCallbacks {
    fun itemAdded(position: Int)

    fun itemRemoved(position: Int)
}