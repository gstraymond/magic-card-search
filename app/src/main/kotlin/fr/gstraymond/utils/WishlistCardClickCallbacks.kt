package fr.gstraymond.utils

import android.content.Context
import com.google.android.material.snackbar.Snackbar
import android.view.View
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.CardClickCallbacks

class WishlistCardClickCallbacks(private val card: Card,
                                 private val context: Context,
                                 private val rootView: View)  : CardClickCallbacks {
    override fun itemAdded(position: Int) {
        showMessage(getMessage(add = true, cardName = card.getLocalizedTitle(context)))
    }

    override fun itemRemoved(position: Int) {
        showMessage(getMessage(add = false, cardName = card.getLocalizedTitle(context)))
    }

    private fun getMessage(add: Boolean, cardName: String): String =
            if (add) String.format(context.resources.getString(R.string.added_to_wishlist), cardName)
            else String.format(context.resources.getString(R.string.removed_from_wishlist), cardName)


    private fun showMessage(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}