package fr.gstraymond.ui.view.impl

import android.app.AlertDialog
import android.content.Context
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.AppCompatButton
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.ShareCardDialogViews
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.*

class ShareView(private val app: CustomApplication,
                private val context: Context,
                private val deckId: Int?,
                private val shareViewCallbacks: ShareViewCallbacks?) : CommonDisplayableView<AppCompatButton>(R.id.card_share) {

    override fun display(view: AppCompatButton, card: Card) = true

    override fun setValue(view: AppCompatButton, card: Card, position: Int) {
        view.supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimary)
        view.setOnClickListener {
            val dialogView = context.inflate(R.layout.dialog_share)
            val clickCallbacks = WishlistCardClickCallbacks(card, context, dialogView)
            val wishList = app.wishList
            val shareCardDialogViews = ShareCardDialogViews(context, wishList, clickCallbacks)
            shareCardDialogViews.display(dialogView, card, 0)

            val otherDecks = app.deckList.filter { it.id != deckId }
            val spinner = dialogView.find<Spinner>(R.id.array_adapter_decks)
            val inWishlist = wishList.contains(card)
            if (otherDecks.isNotEmpty()) {
                spinner.visible()
                spinner.adapter = ArrayAdapter(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        listOf(context.getString(R.string.add_card_to_deck)) + otherDecks.map { it.name })
                spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) = Unit

                    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                        if (position > 0) {
                            val deck = otherDecks[position - 1]
                            val otherCardList = app.cardListBuilder.build(deck.id)
                            val otherDeckCard = DeckCard(card)
                            val message = if (otherCardList.contains(otherDeckCard)) {
                                String.format(context.resources.getString(R.string.already_in_deck), card.title, deck.name)
                            } else {
                                otherCardList.addOrRemove(otherDeckCard)
                                String.format(context.resources.getString(R.string.added_to_deck), card.title, deck.name)
                            }
                            Snackbar.make(dialogView, message, Snackbar.LENGTH_LONG).show()
                        }
                    }

                }
            } else {
                spinner.gone()
            }

            AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton(R.string.close) { _, _ ->
                        if (inWishlist != wishList.contains(card)) {
                            shareViewCallbacks?.wishlistChanged(position)
                        }
                    }
                .create()
                    .show()
        }
    }

    interface ShareViewCallbacks {
        fun wishlistChanged(position: Int)
    }
}