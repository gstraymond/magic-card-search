package fr.gstraymond.ui.adapter

import android.content.Context
import android.view.View
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckCardCallback
import fr.gstraymond.api.ui.view.DisplayableView
import fr.gstraymond.db.json.JsonList
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
                CastingCostView(context),
                CostView(context),
                FavoriteView(cards, clickCallbacks, context.resources)
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
                    app: CustomApplication,
                    deckId: Int,
                    addToSideboard: Boolean,
                    deckCardCallback: DeckCardCallback) :
        CardViews(listOf(
                TitleView(),
                DescriptionView(context),
                CastingCostView(context),
                TypePTView(),
                QuantityView(context, app, deckId, addToSideboard, deckCardCallback),
                CostView(context)
        ))

class DeckDetailCardViews(context: Context,
                          app: CustomApplication,
                          deckId: Int,
                          sideboard: Boolean,
                          deckCardCallback: DeckCardCallback?) : CardViews(listOf(
        TitleView(),
        CastingCostView(context),
        CostView(context),
        QuantityView(context, app, deckId, sideboard, deckCardCallback)
))

class SimpleCardViews : CardViews(listOf(
        TitleView()
))

class ShareCardDialogViews(context: Context,
                           cards: JsonList<Card>,
                           clickCallbacks: CardClickCallbacks) : CardViews(listOf(
        TitleView(),
        FavoriteView(cards, clickCallbacks, context.resources)
))

class CardDetailViews(app: CustomApplication,
                      context: Context,
                      shareViewCallbacks: ShareView.ShareViewCallbacks) :
        CardViews(listOf(
                ShareView(app, context, null, shareViewCallbacks)
        ))

interface CardClickCallbacks {
    fun itemAdded(position: Int)

    fun itemRemoved(position: Int)
}