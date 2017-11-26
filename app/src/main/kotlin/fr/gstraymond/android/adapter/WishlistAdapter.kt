package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.db.json.WishList
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.adapter.WishlistCardViews
import fr.gstraymond.ui.view.impl.ShareView

class WishlistAdapter(app: CustomApplication,
                      context: Context,
                      rootView: View,
                      private val wishList: WishList,
                      private val clickCallbacks: WishlistAdapter.ClickCallbacks) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cardViews = WishlistCardViews(app, context, rootView, ShareViewCallbacks())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.array_adapter_share, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = wishList[position]
        cardViews.display(holder.itemView, card, position)
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
    }

    override fun getItemCount() = wishList.size()

    private inner class ShareViewCallbacks : ShareView.ShareViewCallbacks {

        override fun wishlistChanged(position: Int) {
            notifyItemRemoved(position)
            val total = wishList.size()
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
