package fr.gstraymond.ui.view.impl

import android.content.Context
import android.text.Html
import android.view.View
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.CastingCostFormatter
import fr.gstraymond.ui.view.CommonDisplayableView

class CastingCostView(context: Context) : CommonDisplayableView<TextView>(R.id.array_adapter_card_casting_cost) {

    private val ccFormatter = CastingCostFormatter()
    private val imageGetter = CastingCostImageGetter.small(context)

    override fun setValue(view: TextView, card: Card, position: Int) {
        val castingCost = ccFormatter.format(card.castingCost)
        val html = Html.fromHtml(castingCost, imageGetter, null)

        view.text = html
    }

    override fun display(view: TextView, card: Card) =
            card.castingCost?.isNotEmpty() ?: false
}
