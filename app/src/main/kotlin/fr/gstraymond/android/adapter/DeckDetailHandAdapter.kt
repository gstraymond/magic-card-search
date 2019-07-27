package fr.gstraymond.android.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.HandCardViews

class DeckDetailHandAdapter(context: Context,
                            private val clickCallbacks: ClickCallbacks) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cardViews = HandCardViews(context)

    var cards = listOf<Card>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context)
                            .inflate(R.layout.array_adapter_hand, parent, false)
            ) {}

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {
        val card = cards[position]
        cardViews.display(holder.itemView, card, position)
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
    }

    interface ClickCallbacks {
        fun cardClicked(card: Card)
    }
}