package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.utils.find

class ListView(val context: Context,
               val callbacks: CardDetailAdapter.Callbacks) : View<String>(context, R.layout.card_list) {

    override fun getView(item: String, view: android.view.View): android.view.View {

        val imageView = view.find<ImageView>(R.id.card_list_image)
        imageView.setImageResource(when (item) {
            "wishlist" -> R.drawable.ic_star_white_18dp
            else -> R.drawable.ic_bookmark_border_white_48dp
        })

        val textView = view.find<TextView>(R.id.card_list_title)
        textView.text = when (item) {
            "wishlist" -> context.getString(R.string.wishlist_title)
            else -> callbacks.getDeck(item)?.run { name } ?: "NA"
        }

        return view
    }
}