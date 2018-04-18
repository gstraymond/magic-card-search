package fr.gstraymond.android.adapter

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter.CardTypes.*
import fr.gstraymond.android.adapter.DeckDetailCardsAdapter.ItemTypes.*
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.DeckDetailCardViews
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.drawable
import fr.gstraymond.utils.find
import fr.gstraymond.utils.visible

class DeckDetailCardsAdapter(private val app: CustomApplication,
                             private val context: Context,
                             private val sideboard: Boolean,
                             private val deckId: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var deckCardCallback: DeckCardCallback? = null

    private lateinit var cards: List<Any>

    private val cardViews by lazy {
        DeckDetailCardViews(
                context,
                app,
                deckId,
                sideboard,
                deckCardCallback)
    }

    enum class ItemTypes { HEADER, CARD, EMPTY }

    enum class CardTypes { LAND, CREATURE, INSTANT, OTHER }

    private fun types(type: String) = when {
        type.contains("creature", ignoreCase = true) -> CREATURE
        type.contains("land", ignoreCase = true) -> LAND
        type.contains("instant", ignoreCase = true) -> INSTANT
        type.contains("sorcery", ignoreCase = true) -> INSTANT
        else -> OTHER
    }

    private val cmcComparator = compareBy<DeckCard>(
            { it.card.convertedManaCost },
            { it.card.getLocalizedTitle(context) }
    )
    
    private val colorComparator = compareBy<DeckCard>(
            { it.card.colors.sorted().joinToString() },
            { it.card.land.sorted().joinToString() },
            { it.card.getLocalizedTitle(context) }
    )

    private var comparator = cmcComparator

    fun updateDeckList() {
        val grouped = app.cardListBuilder.build(deckId).all().filter { getMult(it) > 0 }.groupBy { types(it.card.type) }
        cards = CardTypes.values().flatMap {
            grouped[it]?.run {
                val header = getText("card_type_${it.name.toLowerCase()}", "${sumBy { getMult(it) }}")
                listOf(header) + sortedWith(comparator)
            } ?: listOf()
        }
        notifyDataSetChanged()
    }

    private fun getText(textId: String, vararg args: String) =
            String.format(context.resources.getString(getString(textId)), *args)

    private fun getString(id: String) =
            context.resources.getIdentifier(id, "string", context.packageName)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CardViewHolder -> {
                val deckCard = cards[position] as DeckCard
                cardViews.display(holder.itemView, deckCard.card, position)
                holder.itemView.find<TextView>(R.id.array_adapter_text).setOnClickListener {
                    deckCardCallback?.cardClick(deckCard)
                }
            }
            is HeaderViewHolder -> {
                val header = cards[position] as String
                holder.itemView.find<TextView>(R.id.array_adapter_deck_header).text = header
                holder.itemView.find<AppCompatButton>(R.id.array_adapter_deck_header_sort).apply {
                    visible()
                    setCompoundDrawablesWithIntrinsicBounds(context.resources.drawable(R.drawable.ic_sort_white_18dp), null, null, null)
                    supportBackgroundTintList = when (comparator) {
                        cmcComparator -> resources.colorStateList(R.color.colorPrimaryDark)
                        else -> resources.colorStateList(R.color.colorAccent)
                    }
                    setOnClickListener {
                        comparator = when (comparator) {
                            cmcComparator -> colorComparator
                            else -> cmcComparator
                        }
                        updateDeckList()
                    }
                }
            }
        }
    }

    private fun getMult(deckCard: DeckCard) =
            if (sideboard) deckCard.counts.sideboard
            else deckCard.counts.deck

    private val FAB_TOTAL_SIZE = 2

    override fun getItemCount() = cards.size + FAB_TOTAL_SIZE

    override fun getItemViewType(position: Int) = when {
        position >= cards.size -> EMPTY
        cards[position] is DeckCard -> CARD
        else -> HEADER
    }.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).run {
                when (viewType) {
                    CARD.ordinal -> CardViewHolder(inflate(R.layout.array_adapter_deck_card, parent, false))
                    HEADER.ordinal -> HeaderViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false))
                    else -> object : RecyclerView.ViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false)) {}
                }
            }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

interface DeckCardCallback {
    enum class FROM {
        DECK, SB
    }

    fun multChanged(from: DeckCardCallback.FROM, position: Int)
    fun cardClick(deckCard: DeckCard)
}