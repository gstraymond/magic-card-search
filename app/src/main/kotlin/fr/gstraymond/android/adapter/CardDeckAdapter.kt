package fr.gstraymond.android.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gstraymond.R
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.models.Deck
import fr.gstraymond.models.DeckCard
import fr.gstraymond.tools.CastingCostFormatter
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

class CardDeckAdapter(context: Context,
                      private val tuples: List<Pair<Deck, DeckCard.Counts>>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageGetter = CastingCostImageGetter.large(context)
    private val ccFormatter = CastingCostFormatter()

    var onClickListener: (String) -> View.OnClickListener? = { _ -> null }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.array_adapter_card_deck, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (deck, count) = tuples[position]
        val view = holder.itemView

        val deckColors = view.find<TextView>(R.id.array_adapter_deck_colors)
        val deckName = view.find<TextView>(R.id.array_adapter_deck_name)
        val deckSize = view.find<TextView>(R.id.array_adapter_deck_size)

        val colors = DeckStats.colorSymbols(deck.colors)
        if (colors.isEmpty()) {
            deckColors.gone()
        } else {
            deckColors.visible()
            deckColors.text = Html.fromHtml(ccFormatter.format(colors), imageGetter, null)
        }
        deckName.text = deck.name
        deckSize.text = "${count.deck} / ${count.sideboard} / ${count.maybe}"

        view.setOnClickListener(onClickListener(deck.id.toString()))
    }

    override fun getItemCount() = tuples.size
}