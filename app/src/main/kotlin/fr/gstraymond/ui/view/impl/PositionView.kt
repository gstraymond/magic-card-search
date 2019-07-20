package fr.gstraymond.ui.view.impl

import android.content.res.Resources
import android.graphics.Typeface
import android.widget.TextView
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.CommonDisplayableView
import fr.gstraymond.utils.color

class PositionView(private val resources: Resources) :
        CommonDisplayableView<TextView>(R.id.array_adapter_position) {

    override fun setValue(view: TextView, card: Card, position: Int) {
        view.text = "${position + 1}."
        if (position < 7) {
            view.setTextColor(resources.color(R.color.colorAccent))
            view.setTypeface(null, Typeface.BOLD)
        } else {
            view.setTextColor(resources.color(android.R.color.white))
            view.setTypeface(null, Typeface.NORMAL)
        }
    }

    override fun display(view: TextView, card: Card) = true
}
