package fr.gstraymond.ui.view.impl

import android.widget.TextView
import com.magic.card.search.commons.log.Log
import fr.gstraymond.R
import fr.gstraymond.models.search.response.Card
import fr.gstraymond.ui.view.CommonDisplayableView

class CostView : CommonDisplayableView<TextView>(R.id.array_adapter_card_price) {

    private val max1 = 1.0
    private val max2 = 10.0

    private val log = Log(this)

    override fun display(view: TextView, card: Card) = true // display is done after

    override fun setValue(view: TextView, card: Card, position: Int) {
        val prices = card.publications.map { it.price }.filter { it > 0 }.distinct().sorted()

        val text = when {
            prices.any { it <= max1 } -> "$"
            prices.any { it > max1 && it <= max2 } -> "$$"
            prices.any { it > max2 } -> "$$$"
            else -> "?"
        }

        log.d("$text: $prices")

        display(view, text != "?")
        view.text = text
    }
}
