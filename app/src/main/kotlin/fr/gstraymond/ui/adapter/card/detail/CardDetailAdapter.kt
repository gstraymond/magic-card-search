package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import fr.gstraymond.models.Deck
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.Publication
import fr.gstraymond.ui.adapter.card.detail.CardDetailAdapter.ItemTypes.*

class CardDetailAdapter(context: Context,
                        resource: Int,
                        textViewResourceId: Int,
                        objects: List<Any>,
                        callbacks: Callbacks) :
        ArrayAdapter<Any>(context, resource, textViewResourceId, objects) {

    private val cardView = CardView(context, object : InnerCallbacks {
        override fun onImageClick(position: Int) = callbacks.onImageClick(position)

        override fun getDeck(deckId: String): Deck? = getDeck(deckId)

        override fun switchPrice() {
            publicationView.showPaper = !publicationView.showPaper
            this@CardDetailAdapter.notifyDataSetChanged()
        }
    })
    private val publicationView = PublicationView(context)

    interface Callbacks {
        fun onImageClick(position: Int)

        fun getDeck(deckId: String): Deck?
    }

    interface InnerCallbacks {
        fun onImageClick(position: Int)

        fun getDeck(deckId: String): Deck?

        fun switchPrice()
    }

    enum class ItemTypes { CARD, PUBLICATION }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is Card -> CARD
        else -> PUBLICATION
    }.ordinal

    override fun getViewTypeCount() = values().size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) = getItem(position).run {
        when (this) {
            is Card -> cardView.getView(this, convertView, parent)
            else -> publicationView.getView(this as Publication, convertView, parent)
        }
    }
}
