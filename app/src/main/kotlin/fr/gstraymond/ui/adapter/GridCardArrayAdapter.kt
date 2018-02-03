package fr.gstraymond.ui.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import fr.gstraymond.R
import fr.gstraymond.android.presenter.CardListPresenter
import fr.gstraymond.constants.FacetConst
import fr.gstraymond.db.json.JsonList
import fr.gstraymond.glide.CardLoader
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.utils.color
import fr.gstraymond.utils.inflate

class GridCardArrayAdapter(private val context: Context,
                           private val clickCallbacks: ClickCallbacks,
                           private val presenter: CardListPresenter,
                           private val data: CardArrayData) : CardArrayAdapter() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = context
            .inflate(R.layout.grid_adapter_card, parent)
            .run { object : RecyclerView.ViewHolder(this) {} }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = cards[position]
        val pictureView = holder.itemView as ImageView
        holder.itemView.setOnClickListener { clickCallbacks.cardClicked(card) }
        holder.itemView.setOnLongClickListener {
            setBackground(pictureView, !contains(card))
            clickCallbacks.cardLongClicked(card)
        }
        val url = presenter.getCurrentSearch().facets[FacetConst.SET]?.first()?.run {
            card.publications.firstOrNull { it.edition == this }?.image
        } ?: card.publications.map { it.image }.firstOrNull { it != null }

        setBackground(pictureView, contains(card))

        if (url != null) CardLoader(url, card, pictureView).load(context)
    }

    private fun setBackground(pictureView: ImageView,
                              p: Boolean) {
        val bColor = if (p) R.color.gold else android.R.color.transparent
        pictureView.setBackgroundColor(context.resources.color(bColor))
    }

    private fun contains(card: Card) =
        data.cards?.contains(card) ?: data.deck?.second?.contains(DeckCard(card)) ?: false
}

abstract class CardArrayAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    protected val cards = mutableListOf<Card>()

    override fun getItemCount() = cards.size

    fun cards(): List<Card> = cards

    fun setCards(newCards: List<Card>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    fun appendCards(newCards: List<Card>) {
        cards.addAll(newCards)
        notifyItemRangeInserted(cards.size - 1, newCards.size)
    }

    interface ClickCallbacks {

        fun cardClicked(card: Card)

        fun cardLongClicked(card: Card): Boolean
    }
}