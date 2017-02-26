package fr.gstraymond.android.adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.NumberPicker
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.models.DeckLine
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.utils.find


class DeckDetailAdapter(private val cards: List<DeckLine>,
                        private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var deckLineCallback: DeckLineCallback? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deckLine = cards[position]
        val card = deckLine.card

        val mult = holder.itemView.find<TextView>(R.id.array_adapter_deck_card_mult)
        mult.text = deckLine.mult.toString()
        mult.setOnClickListener {
            val view = LayoutInflater.from(context).inflate(R.layout.array_adapter_deck_card_mult, null)
            val picker = view.find<NumberPicker>(R.id.array_adapter_deck_card_mult).apply {
                minValue = 0
                maxValue = 100
                wrapSelectorWheel = false
            }

            AlertDialog.Builder(context)
                    .setView(view)
                    .setPositiveButton(android.R.string.ok, { dialog, which ->
                        deckLineCallback?.multChanged(deckLine, picker.value)
                    })
                    .setNegativeButton(android.R.string.cancel, { dialog, which -> })
                    .create()
                    .show()
        }

        val title = holder.itemView.find<TextView>(R.id.array_adapter_deck_card_name)
        title.text = card.getLocalizedTitle(context)

        val sideboard = holder.itemView.find<CheckBox>(R.id.array_adapter_deck_card_sideboard)
        sideboard.isChecked = deckLine.isSideboard
    }

    override fun getItemCount() = cards.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.array_adapter_deck_card, parent, false)
                    .run { object : RecyclerView.ViewHolder(this) {} }
}

interface DeckLineCallback {

    fun multChanged(deckLine: DeckLine, mult: Int)
}