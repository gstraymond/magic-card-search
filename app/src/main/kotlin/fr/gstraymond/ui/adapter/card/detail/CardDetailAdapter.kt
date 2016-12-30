package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.Publication
import fr.gstraymond.ui.adapter.card.detail.CardDetailAdapter.ItemTypes.*

class CardDetailAdapter(context: Context,
                        resource: Int,
                        textViewResourceId: Int,
                        objects: List<Any>,
                        callbacks: Callbacks) :
        ArrayAdapter<Any>(context, resource, textViewResourceId, objects) {

    private val cardView = CardView(context, callbacks)
    private val publicationView = PublicationView(context)
    private val listView = ListView(context)

    interface Callbacks {
        fun onImageClick(position: Int)
    }

    enum class ItemTypes { CARD, PUBLICATION, DECK }

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is Card -> CARD
        is Publication -> PUBLICATION
        else -> DECK
    }.ordinal

    override fun getViewTypeCount() = values().size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) = getItem(position).run {
        when (this) {
            is Card -> cardView.getView(this, convertView, parent)
            is Publication -> publicationView.getView(this, convertView, parent)
            else -> listView.getView(this as String, convertView, parent)
        }
    }
}
