package fr.gstraymond.android.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.db.json.CardWithOccurrence

class DeckDetailAdapter(private val cards: List<CardWithOccurrence>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val cardWithOccurrence = cards[position]
        val card = cardWithOccurrence.card
        val textView = holder.itemView.findViewById(R.id.array_adapter_deck_name) as TextView
        textView.text = "${if (cardWithOccurrence.isSideboard) "SB: " else ""}[${cardWithOccurrence.occurrence}] ${card.title}"
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }
}