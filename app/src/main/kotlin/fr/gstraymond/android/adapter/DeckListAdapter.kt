package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.Colors
import fr.gstraymond.models.Deck
import fr.gstraymond.tools.CastingCostFormatter
import java.util.*


class DeckListAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageGetter = CastingCostImageGetter.large(context)
    private val ccFormatter = CastingCostFormatter()

    private val selectedItems = SparseBooleanArray()

    var decks: List<Deck> = listOf()

    var onClickListener: (String) -> View.OnClickListener? = { deckId -> null }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deck = decks[position]

        val deckColors = holder.itemView.findViewById(R.id.array_adapter_deck_colors) as TextView
        val deckFormat = holder.itemView.findViewById(R.id.array_adapter_deck_format) as TextView
        val deckName = holder.itemView.findViewById(R.id.array_adapter_deck_name) as TextView

        val castingCost = deck.colors.map { Colors.mainColorsMap[it] }.sortedBy { it }.joinToString(" ")
        deckColors.text = Html.fromHtml(ccFormatter.format(castingCost), imageGetter, null)
        deckFormat.text = deck.format
        deckName.text = deck.name
        holder.itemView.setOnClickListener(onClickListener(deck.id.toString()))
    }

    override fun getItemCount() = decks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }

    // selection
    // https://developer.android.com/guide/topics/ui/menus.html#CAB
    // http://www.grokkingandroid.com/statelistdrawables-for-recyclerview-selection/

    fun toggleSelection(pos: Int) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos)
        } else {
            selectedItems.put(pos, true)
        }
        notifyItemChanged(pos)
    }

    fun clearSelections() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemCount() = selectedItems.size()

    fun getSelectedItems(): List<Int> {
        val items = ArrayList<Int>(selectedItems.size())
        (0..selectedItems.size() - 1).mapTo(items) { selectedItems.keyAt(it) }
        return items
    }
}