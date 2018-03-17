package fr.gstraymond.android.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.android.CustomApplication
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.DECK
import fr.gstraymond.android.adapter.DeckCardCallback.FROM.SB
import fr.gstraymond.models.DeckCard
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.DeckDetailCardViews
import fr.gstraymond.ui.adapter.SimpleCardViews
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate

class DeckDetailCardsAdapter(private val app: CustomApplication,
                             private val context: Context,
                             private val sideboard: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var deckId: Int = 0

    var deckCardCallback: DeckCardCallback? = null

    private lateinit var cards: List<Any>

    private val cardViews by lazy { SimpleCardViews(context) }

    enum class ItemTypes { HEADER, CARD, EMPTY }

    enum class CardTypes { LAND, CREATURE, INSTANT, OTHER }

    private fun types(type: String) = when {
        type.contains("creature", ignoreCase = true) -> CardTypes.CREATURE
        type.contains("land", ignoreCase = true) -> CardTypes.LAND
        type.contains("instant", ignoreCase = true) -> CardTypes.INSTANT
        type.contains("sorcery", ignoreCase = true) -> CardTypes.INSTANT
        else -> CardTypes.OTHER
    }

    private val comparator = compareBy<DeckCard>({ it.card.convertedManaCost }, { it.card.getLocalizedTitle(context) })

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
                val card = deckCard.card

                DeckDetailCardViews(context).display(holder.itemView, card, position)

                val mult = holder.itemView.find<AppCompatButton>(R.id.array_adapter_deck_card_mult)
                mult.supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimaryDark)
                mult.text = "${getMult(deckCard)}"
                mult.setOnClickListener {
                    val view = context.inflate(R.layout.array_adapter_deck_card_mult)
                    cardViews.display(view, card, position)
                    val deckCount = view.find<TextView>(R.id.array_adapter_deck_card_mult).apply {
                        text = deckCard.counts.deck.toString()
                    }
                    val sbCount = view.find<TextView>(R.id.array_adapter_deck_sb_mult).apply {
                        text = deckCard.counts.sideboard.toString()
                    }

                    listOf("card", "sb").forEach { line ->
                        val multView = view.find<TextView>(getId("array_adapter_deck_${line}_mult"))
                        listOf("add", "remove").forEach { action ->
                            val coef = if (action == "add") 1 else -1
                            listOf(1, 4).forEach { mult ->
                                view.find<AppCompatButton>(getId("array_adapter_deck_${line}_${action}_$mult")).apply {
                                    supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimary)
                                    setOnClickListener {
                                        val m = multView.text.toString().toInt()
                                        multView.text = Math.min(99, Math.max(0, m + mult * coef)).toString()
                                    }
                                }
                            }
                        }
                    }

                    AlertDialog.Builder(context)
                            .setView(view)
                            .setPositiveButton(android.R.string.ok, { _, _ ->
                                val pickerDeckMult = deckCount.text.toString().toInt()
                                val pickerSbMult = sbCount.text.toString().toInt()
                                deckCardCallback?.multChanged(deckCard, if (sideboard) SB else DECK, pickerDeckMult, pickerSbMult)
                            })
                            .setNegativeButton(android.R.string.cancel, { _, _ -> })
                            .create()
                            .show()
                }

                holder.itemView.find<TextView>(R.id.array_adapter_text).setOnClickListener {
                    deckCardCallback?.cardClick(deckCard)
                }
            }
            is HeaderViewHolder -> {
                val header = cards[position] as String
                holder.itemView.find<TextView>(R.id.array_adapter_deck_header).text = header
            }
        }
    }

    private fun getId(id: String) =
            context.resources.getIdentifier(id, "id", context.packageName)

    private fun getMult(deckCard: DeckCard) =
            if (sideboard) deckCard.counts.sideboard
            else deckCard.counts.deck

    private val FAB_TOTAL_SIZE = 2

    override fun getItemCount() = cards.size + FAB_TOTAL_SIZE

    override fun getItemViewType(position: Int) =
            if (position >= cards.size) ItemTypes.EMPTY.ordinal
            else {
                when (cards[position]) {
                    is DeckCard -> ItemTypes.CARD
                    else -> ItemTypes.HEADER
                }.ordinal
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater.from(parent.context).run {
                when (viewType) {
                    ItemTypes.CARD.ordinal -> CardViewHolder(inflate(R.layout.array_adapter_deck_card, parent, false))
                    ItemTypes.HEADER.ordinal -> HeaderViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false))
                    else -> object : RecyclerView.ViewHolder(inflate(R.layout.array_adapter_deck_header, parent, false)) {}
                }
            }
}

class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)
class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)

interface DeckCardCallback {
    enum class FROM {
        DECK, SB
    }

    fun multChanged(deckCard: DeckCard, from: FROM, deck: Int, sideboard: Int)
    fun cardClick(deckCard: DeckCard)
}