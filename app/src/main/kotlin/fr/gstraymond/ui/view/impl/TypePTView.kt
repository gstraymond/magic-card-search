package fr.gstraymond.ui.view.impl

import android.widget.TextView

import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.PowerToughnessFormatter
import fr.gstraymond.tools.TypeFormatter
import fr.gstraymond.ui.view.CommonDisplayableView

class TypePTView : CommonDisplayableView<TextView>(R.id.array_adapter_card_type_pt) {

    private val ptFormatter = PowerToughnessFormatter()
    private val typeFormatter = TypeFormatter()

    override fun setValue(view: TextView, card: Card, position: Int) {
        val ptOrType = getPtOrType(card)
        if (ptOrType.isEmpty()) view.text = ""
        else view.text = " — $ptOrType"
    }

    override fun display(view: TextView, card: Card) = display(view, true)

    private fun getPtOrType(card: Card): String {
        val pt = ptFormatter.format(card)
        return when {
            card.loyalty != null -> card.loyalty
            pt.isNotEmpty() -> pt
            else -> typeFormatter.formatFirst(card)
        }
    }
}
