package fr.gstraymond.ui.view.impl

import android.content.Context
import android.text.Html
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.CastingCostImageGetter
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.DescriptionFormatter
import fr.gstraymond.ui.view.CommonDisplayableView

class DescriptionView(context: Context) : CommonDisplayableView<TextView>(R.id.array_adapter_description) {

    private val descFormatter = DescriptionFormatter()
    private val imageGetter = CastingCostImageGetter.small(context)

    override fun setValue(view: TextView, card: Card, position: Int) {
        val desc = descFormatter.format(card, false)
        val html = Html.fromHtml(desc, imageGetter, null)

        view.text = html
    }

    override fun display(view: TextView, card: Card) = display(view, card.description.isNotEmpty())
}
