package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.db.json.Wishlist
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.CardClickCallbacks
import fr.gstraymond.ui.adapter.WishlistCardViews

class WishlistAdapter(context: Context,
                      private val wishlist: Wishlist,
                      private val clickCallbacks: WishlistAdapter.ClickCallbacks) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cardViews = WishlistCardViews(context, wishlist, FavoriteViewClickCallbacks())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.array_adapter_card, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = wishlist[position]
        cardViews.display(holder.itemView, card, position)
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
    }

    override fun getItemCount() = wishlist.size()

    private inner class FavoriteViewClickCallbacks : CardClickCallbacks {

        override fun itemAdded(position: Int) {}

        override fun itemRemoved(position: Int) {
            notifyItemRemoved(position)
            val total = wishlist.size()
            if (position < total) {
                notifyItemRangeChanged(position, total - position)
            }
            if (total == 0) {
                clickCallbacks.onEmptyList()
            }
        }
    }

    interface ClickCallbacks {
        fun onEmptyList()

        fun cardClicked(card: Card)
    }
}
