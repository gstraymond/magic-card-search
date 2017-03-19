package fr.gstraymond.android.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.db.json.CardList
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.ui.adapter.DeckDetailCardViews
import fr.gstraymond.utils.colorStateList
import fr.gstraymond.utils.find
import fr.gstraymond.utils.inflate
import java.util.*


class DeckDetailCardsAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cardList: CardList

    var deckLineCallback: DeckLineCallback? = null

    private val cardViews = DeckDetailCardViews(context)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deckLine = cardList.all().sortedWith(cardComparator)[position]
        val card = deckLine.card
        cardViews.display(holder.itemView, card, position)

        val mult = holder.itemView.find<AppCompatButton>(R.id.array_adapter_deck_card_mult)
        mult.supportBackgroundTintList = context.resources.colorStateList(R.color.colorPrimaryDark)
        mult.text = "${deckLine.mult}"
        mult.setOnClickListener {
            val view = context.inflate(R.layout.array_adapter_deck_card_mult)
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
        compare({ c1.isSideboard.compareTo(c2.isSideboard) }, {
            compare({ c1.card.convertedManaCost.compareTo(c2.card.convertedManaCost) },
                    { c1.card.getLocalizedTitle(context).compareTo(c2.card.getLocalizedTitle(context)) })
        })
    }

    private fun compare(f: () -> Int, f2: () -> Int): Int {
        val comparison = f()
        return when (comparison) {
            0 -> f2()
            else -> comparison
        }
    }
}

interface DeckLineCallback {

    fun multChanged(deckLine: DeckLine, mult: Int)
    fun sideboardChanged(deckLine: DeckLine, sideboard: Boolean)
    fun cardClick(deckLine: DeckLine)
}