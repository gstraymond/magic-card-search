package fr.gstraymond.ui.view.impl

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.text.Html
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.tools.CardColorUtil
import fr.gstraymond.tools.LanguageUtil
import fr.gstraymond.ui.view.CommonDisplayableView

class TitleView(context: Context) : CommonDisplayableView<TextView>(R.id.array_adapter_text) {

    private val showFrenchTitle = LanguageUtil.showFrench(context)
    private val resources = context.resources

    override fun setValue(view: TextView, card: Card, position: Int) {
        val color = CardColorUtil.getColorId(card.colors, card.type)
        view.text = Html.fromHtml("<b>" + getTitle(card) + "</b>")
        view.setTextColor(ResourcesCompat.getColor(resources, color, null))
    }

    override fun display(view: TextView, card: Card) = display(view, true)

    private fun getTitle(card: Card) = when {
        (showFrenchTitle && card.frenchTitle != null) -> card.frenchTitle
        else -> card.title
    }
}
