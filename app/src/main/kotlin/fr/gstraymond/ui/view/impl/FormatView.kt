package fr.gstraymond.ui.view.impl

import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.biz.Formats
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.CommonDisplayableView

class FormatView : CommonDisplayableView<TextView>(0/*R.id.array_adapter_card_format*/) {

    override fun setValue(view: TextView, card: Card, position: Int) {
        view.text = card.formats.maxBy { Formats.ordered.indexOf(it) }
    }

    override fun display(view: TextView, card: Card) = true
}
