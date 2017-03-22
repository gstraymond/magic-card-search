package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.Colors
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.models.Deck
import fr.gstraymond.tools.CastingCostFormatter
import fr.gstraymond.utils.find

class DeckListAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageGetter = CastingCostImageGetter.large(context)
    private val ccFormatter = CastingCostFormatter()

    var decks: List<Deck> = listOf()

    var onClickListener: (String) -> View.OnClickListener? = { _ -> null }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deck = decks[position]
        val view = holder.itemView

        val deckColors = view.find<TextView>(R.id.array_adapter_deck_colors)
        val deckFormat = view.find<TextView>(R.id.array_adapter_deck_format)
        val deckName = view.find<TextView>(R.id.array_adapter_deck_name)
        val deckSize = view.find<TextView>(R.id.array_adapter_deck_size)

        val colors = DeckStats.colorSymbols(deck.colors)
        if (colors.isEmpty()) {
            deckColors.visibility = View.GONE
        } else {
            deckColors.visibility = View.VISIBLE
            deckColors.text = Html.fromHtml(ccFormatter.format(colors), imageGetter, null)
        }
        deckFormat.text = deck.format
        deckName.text = deck.name
        deckSize.text = "${deck.deckSize} / ${deck.sideboardSize}"

        view.setOnClickListener(onClickListener(deck.id.toString()))
    }

    override fun getItemCount() = decks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }
}