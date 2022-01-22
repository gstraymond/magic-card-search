package fr.gstraymond.android.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.gstraymond.R
import fr.gstraymond.android.DecksActivity.SortTypes
import fr.gstraymond.android.adapter.DeckListAdapter.ItemTypes.*
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.biz.DeckStats
import fr.gstraymond.models.Deck
import fr.gstraymond.tools.CastingCostFormatter
import fr.gstraymond.utils.find
import fr.gstraymond.utils.gone
import fr.gstraymond.utils.visible

class DeckListAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val imageGetter = CastingCostImageGetter.large(context)
    private val ccFormatter = CastingCostFormatter()

    private var items: List<Any> = listOf()

    var sort: SortTypes = SortTypes.Format

    var colorFilters: MutableList<String> = mutableListOf()

    var decks: List<Deck> = listOf()
        set(value) {
            val filteredDecks = value.filter { deck ->
                colorFilters.all { deck.colors.contains(it) }
            }
            items = when (sort) {
                SortTypes.Format -> filteredDecks.groupBy {
                    it.maybeFormat ?: context.getString(R.string.select_format)
                }
                        .toList()
                        .fold(listOf()) { acc, (headers, decks) ->
                            acc + listOf("$headers (${decks.size})") + decks
                        }
                SortTypes.Alpha -> filteredDecks.sortedBy { it.name.lowercase() }
            }

            field = filteredDecks
        }

    var onClickListener: (String) -> View.OnClickListener? = { _ -> null }

    enum class ItemTypes { HEADER, DECK, EMPTY }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DeckViewHolder -> {
                val deck = items[position] as Deck
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
                deckSize.text = when {
                    deck.isCommander() -> "${deck.deckSize + deck.sideboardSize}"
                    else -> "${deck.deckSize} / ${deck.sideboardSize}"
                }

                view.setOnClickListener(onClickListener(deck.id.toString()))
            }
            is HeaderViewHolder -> {
                val header = items[position] as String
                holder.itemView.find<TextView>(R.id.array_adapter_deck_header).text = header
            }
        }
    }

    private val FAB_TOTAL_SIZE = 2

    override fun getItemCount() = items.size + FAB_TOTAL_SIZE

    override fun getItemViewType(position: Int) = when {
        position >= items.size -> EMPTY
        items[position] is Deck -> DECK
        else -> HEADER
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).run {
                when (viewType) {
                    DECK.ordinal -> DeckViewHolder(inflate(R.layout.array_adapter_deck, parent, false))
                    HEADER.ordinal -> HeaderViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false))
                    else -> object : RecyclerView.ViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false)) {}
                }
            }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class DeckViewHolder(view: View) : RecyclerView.ViewHolder(view)
}