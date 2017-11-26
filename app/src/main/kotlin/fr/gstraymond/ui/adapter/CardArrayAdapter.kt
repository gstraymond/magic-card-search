package fr.gstraymond.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.DataUpdater
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.utils.inflate

class LinearCardArrayAdapter(private val view: View,
                             private val data: CardArrayData,
                             private val clickCallbacks: ClickCallbacks,
                             private val cardClickCallbacks: CardClickCallbacks,
                             private val dataUpdater: DataUpdater) : CardArrayAdapter() {

    private val context = view.context

    private val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = context
            .inflate(R.layout.array_adapter_card, parent)
            .run { object : RecyclerView.ViewHolder(this) {} }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]
        cardViews.display(holder.itemView, card, position)
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
        holder.itemView.setOnLongClickListener {
            clipboard.primaryClip = ClipData.newPlainText("card title", card.getLocalizedTitle(context))
            showMessage(context.resources.getString(R.string.added_to_clipboard))
            true
        }
    }

    private val cardViews = data.cards?.run {
        FavoriteCardViews(context, this, FavoriteViewClickCallbacks())
    } ?: DeckCardViews(context, data.deck!!.second, FavoriteViewClickCallbacks(), dataUpdater.getCurrentSearch().addToSideboard)

    private inner class FavoriteViewClickCallbacks : CardClickCallbacks {

        override fun itemAdded(position: Int) {
            val message = getMessage(add = true, cardName = cards[position].getLocalizedTitle(context))
            showMessage(message)
            cardClickCallbacks.itemAdded(position)
        }

        override fun itemRemoved(position: Int) {
            val message = getMessage(add = false, cardName = cards[position].getLocalizedTitle(context))
            showMessage(message)
            cardClickCallbacks.itemRemoved(position)
        }
    }

    private fun getMessage(add: Boolean, cardName: String): String = if (data.deck != null) {
        if (add) String.format(context.resources.getString(R.string.added_to_deck), cardName, data.deck.first.name)
        else String.format(context.resources.getString(R.string.removed_from_deck), cardName, data.deck.first.name)
    } else {
        if (add) String.format(context.resources.getString(R.string.added_to_wishlist), cardName)
        else String.format(context.resources.getString(R.string.removed_from_wishlist), cardName)
    }

    private fun showMessage(message: String) {
        dataUpdater.getLoadingSnackbar()?.dismiss()
        val snackbar = Snackbar.make(view, message, LENGTH_LONG)
        snackbar.show()
        dataUpdater.setLoadingSnackbar(snackbar)
    }
}

data class CardArrayData(val cards: JsonList<Card>?,
                         val deck: Pair<Deck, JsonList<DeckCard>>?)