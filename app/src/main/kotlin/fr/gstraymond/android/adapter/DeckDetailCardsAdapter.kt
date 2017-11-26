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
import fr.gstraymond.utils.*
import java.util.*

class DeckDetailCardsAdapter(private val app: CustomApplication,
                             private val context: Context,
                             private val sideboard: Boolean,
                             private val rootView: View) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var deckId: Int = 0

    var deckCardCallback: DeckCardCallback? = null

    private lateinit var cards: List<DeckCard>

    private val cardViews by lazy { SimpleCardViews(context) }

    fun updateDeckList() {
        cards = app.cardListBuilder.build(deckId).all().filter { getMult(it) > 0 }.sortedWith(cardComparator)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val root = holder.itemView.findView(R.id.array_adapter_deck_card_root)
        if (position >= cards.size) {
            root.invisible()
            return
        }

        root.visible()
        val deckCard = cards[position]
        val card = deckCard.card

        DeckDetailCardViews(app, context, rootView, deckId).display(holder.itemView, card, position)

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

    private fun getId(id: String) =
            context.resources.getIdentifier(id, "id", context.packageName)

    private fun getMult(deckCard: DeckCard) =
            if (sideboard) deckCard.counts.sideboard
            else deckCard.counts.deck

    private val FAB_TOTAL_SIZE = 1

    override fun getItemCount() = cards.size + FAB_TOTAL_SIZE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck_card, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }


    private val cardComparator = Comparator<DeckCard> { (card1), (card2) ->
        compare({ card1.convertedManaCost.compareTo(card2.convertedManaCost) },
                { card1.getLocalizedTitle(context).compareTo(card2.getLocalizedTitle(context)) })
    }

    private fun compare(f: () -> Int, f2: () -> Int): Int {
        val comparison = f()
        return when (comparison) {
            0 -> f2()
            else -> comparison
        }
    }
}

interface DeckCardCallback {
    enum class FROM {
        DECK, SB
    }

    fun multChanged(deckCard: DeckCard, from: FROM, deck: Int, sideboard: Int)
    fun cardClick(deckCard: DeckCard)
}