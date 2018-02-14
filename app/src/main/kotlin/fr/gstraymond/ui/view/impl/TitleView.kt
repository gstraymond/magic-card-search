package fr.gstraymond.ui.view.impl

import android.text.Html
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.models.search.response.getLocalizedTitle
import fr.gstraymond.tools.CardColorUtil
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.color

class TitleView :
        CommonDisplayableView<TextView>(R.id.array_adapter_text) {

    override fun setValue(view: TextView, card: Card, position: Int) {
        val color = CardColorUtil.getColorId(card.colors, card.type, card.land ?: listOf())
        view.apply {
            text = Html.fromHtml("<b>${card.getLocalizedTitle(context)}</b>")
            setTextColor(context.resources.color(color))
        }
    }

    override fun display(view: TextView, card: Card) = true
}
