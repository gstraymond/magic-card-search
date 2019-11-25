package fr.gstraymond.android.adapter

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout.LayoutParams
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckDetailSpoilerAdapter.CardTypes.*
import fr.gstraymond.biz.PictureRequestListener
import fr.gstraymond.glide.CardLoader
import fr.gstraymond.models.search.response.Card

class DeckDetailSpoilerAdapter(val app: CustomApplication,
                               private val clickCallbacks: ClickCallbacks)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(), PictureRequestListener.Callbacks {

    var pairs = listOf<Pair<Card, Int>>()


    private lateinit var elements: List<Any>


    enum class ItemTypes { HEADER, CARD }

    // FIXME dup
    enum class CardTypes { LAND, CREATURE, INSTANT, ARTIFACT, PLANESWALKER, ENCHANTMENT, OTHER }

    // FIXME dup
    private fun types(type: String) = when {
        type.contains("creature", ignoreCase = true) -> CREATURE
        type.contains("land", ignoreCase = true) -> LAND
        type.contains("instant", ignoreCase = true) -> INSTANT
        type.contains("sorcery", ignoreCase = true) -> INSTANT
        type.contains("artifact", ignoreCase = true) -> ARTIFACT
        type.contains("planeswalker", ignoreCase = true) -> PLANESWALKER
        type.contains("enchantment", ignoreCase = true) -> ENCHANTMENT
        else -> OTHER
    }

    fun updateDeckList() {
        val grouped = app.cardListBuilder.build(deckId).all().groupBy { types(it.card.type) }
        elements = CardTypes.values().flatMap {
            grouped[it]?.run {
                val header = getText("card_type_${it.name.toLowerCase()}", "${sumBy { getMult(it) }}")
                listOf(header) + sortedWith(comparator)
            } ?: listOf()
        }
        notifyDataSetChanged()
    }

    private val defaultDisplay = (context as Activity).windowManager.defaultDisplay

    private val imageWidth = DisplayMetrics()
            .apply { defaultDisplay.getMetrics(this) }
            .run { widthPixels / 2 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            LayoutInflater.from(parent.context).run {
                when (viewType) {
                    ItemTypes.CARD.ordinal -> CardViewHolder(RelativeLayout(context))
                    else -> HeaderViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false))
                }
            }

    override fun getItemCount() = pairs.size


    override fun getItemViewType(position: Int) = when {
        elements[position] is Pair<*, *> -> ItemTypes.CARD
        else -> ItemTypes.HEADER
    }.ordinal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {

        when (holder) {
            is HeaderViewHolder -> {

            }
            is CardViewHolder -> {
                val (card, mult) = pairs[position]
                val parentView = holder.itemView
                when (parentView) {
                    is RelativeLayout -> {
                        parentView.removeAllViews()

                        // FIXME null
                        val url = card.publications.map { it.image }.firstOrNull { it != null }
                        (1..mult).forEach {
                            val imageView = ImageView(context).apply {
                                id = 0x123456 + it + position * 100
                                layoutParams = LayoutParams(imageWidth, imageWidth).apply {
                                    setMargins(0, (width / 9) * (it - 1), 0, 0)
                                }
                            }

                            CardLoader(url, card, imageView, PictureRequestListener(url, this)).load(context, 2)
                            parentView.addView(imageView)
                        }
                    }
                }

                parentView.setOnClickListener { clickCallbacks.cardClicked(card) }
            }
        }
    }

    interface ClickCallbacks {
        fun cardClicked(card: Card)
    }

    override fun onDownloadComplete() {}

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class CardViewHolder(view: RelativeLayout) : RecyclerView.ViewHolder(view)
}