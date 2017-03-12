package fr.gstraymond.android.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.DeckDetailCardViews
import fr.gstraymond.utils.find
import java.util.*


class DeckDetailCardsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cardList: CardList

    var deckLineCallback: DeckLineCallback? = null

    private val cardViews = DeckDetailCardViews(context)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deckLine = cardList.all().sortedWith(cardComparator)[position]
        val card = deckLine.card
        cardViews.display(holder.itemView, card, position)

        val mult = holder.itemView.find<Button>(R.id.array_adapter_deck_card_mult)
        mult.text = "${deckLine.mult}"
        mult.setOnClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.array_adapter_deck_card_mult, null)
            val picker = view.find<NumberPicker>(R.id.array_adapter_deck_card_mult).apply {
                minValue = 0
                maxValue = 100
                value = deckLine.mult
                wrapSelectorWheel = false
            }

            AlertDialog.Builder(context)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { _, _ ->
                        val pickerMult = picker.value
                        deckLineCallback?.multChanged(deckLine, pickerMult)
                        when (pickerMult) {
                            0 -> notifyItemRemoved(position)
                            else -> notifyItemChanged(position)
                        }

                    })
                    .setNegativeButton(android.R.string.cancel, { _, _ -> })
                    .create()
                    .show()
        }

        holder.itemView.find<TextView>(R.id.array_adapter_text).setOnClickListener {
            deckLineCallback?.cardClick(deckLine)
        }

        val sideboard = holder.itemView.find<CheckBox>(R.id.array_adapter_deck_card_sideboard)
        sideboard.isChecked = deckLine.isSideboard
        sideboard.setOnClickListener {
            deckLineCallback?.sideboardChanged(deckLine, sideboard.isChecked)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = cardList.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck_card, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }


    private val cardComparator = Comparator<DeckLine> { c1, c2 ->
        val z1 = if (c1.isSideboard) 1000 else -1000
        val z2 = if (c2.isSideboard) -1000 else 1000
        val cmcCompare = c1.card.convertedManaCost.compareTo(c2.card.convertedManaCost)
        val compare = when(cmcCompare) {
            0 -> c1.card.title.compareTo(c2.card.title)
            else -> cmcCompare
        }
        z1 + z2 + compare
    }
}

interface DeckLineCallback {

    fun multChanged(deckLine: DeckLine, mult: Int)
    fun sideboardChanged(deckLine: DeckLine, sideboard: Boolean)
    fun cardClick(deckLine: DeckLine)
}