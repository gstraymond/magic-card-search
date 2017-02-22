package fr.gstraymond.ui.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import fr.gstraymond.R
import fr.gstraymond.db.json.Wishlist
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.impl.FavoriteView


class CardArrayAdapter(private val context: Context,
                       wishlist: Wishlist,
                       private val clickCallbacks: ClickCallbacks) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cards = mutableListOf<Card>()
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.array_adapter_card, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]
        cardViews.display(holder.itemView, card, position)
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
        holder.itemView.setOnLongClickListener {
            clipboard.primaryClip = ClipData.newPlainText("card title", card.title)
            val message = context.resources.getString(R.string.added_to_clipboard)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            true
        }
    }

    override fun getItemCount() = cards.size

    private val cardViews = CardViews(context, wishlist, FavoriteViewClickCallbacks(context))

    private inner class FavoriteViewClickCallbacks(val context: Context) : FavoriteView.ClickCallbacks {

        override fun itemAdded(position: Int) {
            notifyItemChanged(position)
            val message = String.format(context.resources.getString(R.string.added_to_wishlist), cards[position].title)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        override fun itemRemoved(position: Int) {
            notifyItemChanged(position)
            val message = String.format(context.resources.getString(R.string.removed_from_wishlist), cards[position].title)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun setCards(newCards: List<Card>): Unit {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    fun appendCards(newCards: List<Card>): Unit {
        cards.addAll(newCards)
        //notifyItemRangeInserted(cards.size - 1, newCards.size)
        notifyDataSetChanged()
    }

    interface ClickCallbacks {

        fun cardClicked(card: Card)
    }
}
