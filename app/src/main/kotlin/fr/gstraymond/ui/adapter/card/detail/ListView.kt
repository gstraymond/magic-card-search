package fr.gstraymond.ui.adapter.card.detail

import android.content.Context
import android.widget.TextView
import fr.gstraymond.R

class ListView(context: Context) : View<String>(context, R.layout.card_list) {

    override fun getView(item: String, view: android.view.View): android.view.View {
        val deckItem = view.find<TextView>(R.id.card_list_title)
        deckItem.text = item
        return view
    }
}